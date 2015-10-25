package parser.ratp

import java.io.{File, FilenameFilter}
import java.time.{Duration, LocalDate, LocalTime, ZoneOffset}

import algo.{CSA, Connection, Timetable}
import com.github.tototoshi.csv._

import scala.annotation.tailrec

object Parser {

  case class GtfsData(name: String, routes: Iterable[Route], trips: Iterable[Trip], stops: Iterable[Stop], stopTimes: Iterable[StopTime], transfers: Iterable[Transfer]) {
    lazy val stopTimesByTripId = stopTimes.groupBy(_.tripId)
    lazy val stopTimesByStopId = stopTimes.groupBy(_.stopId)
    lazy val stopsByStopId: Map[Long, Stop] = stops.map(stop => stop.stopId -> stop)(collection.breakOut)

    override def toString: String = name
  }

  def main(args: Array[String]) {

    val gtfsRootDirectory = new File(args(0))
    val lineDirectories: Array[File] = gtfsRootDirectory.
      listFiles(new FilenameFilter() {
        override def accept(dir: File, name: String) = name.startsWith("RATP_GTFS_")
      }).
      filter(_.isDirectory)

    val lines: Iterable[GtfsData] = lineDirectories.map { directory =>
      val routes = CSVReader.open(new File(directory, "routes.txt")).allWithHeaders().map(Route.parse)
      val trips = CSVReader.open(new File(directory, "trips.txt")).allWithHeaders().map(Trip.parse)
      val stops = CSVReader.open(new File(directory, "stops.txt")).allWithHeaders().map(Stop.parse)
      val stopTimes = CSVReader.open(new File(directory, "stop_times.txt")).allWithHeaders().map(StopTime.parse)
      val transfers = CSVReader.open(new File(directory, "transfers.txt")).allWithHeaders().map(Transfer.parse)

      val name: String = directory.getName.split("_").last

      GtfsData(name, routes, trips, stops, stopTimes, transfers)
    }

    val mergedGtfsData = GtfsData(
      "RATP",
      lines.flatMap(_.routes),
      lines.flatMap(_.trips),
      lines.flatMap(_.stops),
      lines.flatMap(_.stopTimes),
      lines.flatMap(_.transfers)
    )


    // Grouping by tripId to avoid trips folding over each other and have "bad" connections.
    // Eg.:
    // Trip I:  A (t1) -----------------------> B (t3)
    // Trip II:                C (t2) --------------------------> D (t4)
    // Without trip: would get connections from A to C, C to B and B to D.
    val connectionsFromStopTimes = mergedGtfsData.stopTimesByTripId.values.flatMap(stopTimesToConnections)
    val connectionsFromTransfers = transfersToConnections(mergedGtfsData)

    val connections = (connectionsFromStopTimes ++ connectionsFromTransfers).toList.sortBy(_.arrivalTimestamp)
    val timetable = Timetable(connections)

    val montparnasseBienvenue = 1827
    val voltaireLeonBlum = 1633
    val start: Long = System.currentTimeMillis()

    CSA(timetable, mergedGtfsData.stopsByStopId).compute(montparnasseBienvenue, voltaireLeonBlum, durationToTimestamp(Duration.ofHours(18)))

    val elapsed = System.currentTimeMillis() - start
    println(s"Solution found in $elapsed ms")
  }

  private def stopTimesToConnections(stopTimes: Iterable[StopTime]): Iterable[Connection] = {
    @tailrec
    def loop(stopTimes: Iterable[StopTime], connections: List[Connection]): List[Connection] = {
      stopTimes match {
        case Nil => connections
        case head :: Nil => connections
        case head :: tail =>
          val departureStop = head.stopId.toInt
          val arrivalStop = tail.head.stopId.toInt
          val departureTime = durationToTimestamp(head.departureTime)
          val arrivalTime = durationToTimestamp(tail.head.departureTime)
          val connection: Connection = Connection(departureStop, arrivalStop, departureTime, arrivalTime)
          loop(tail, connections :+ connection)
      }
    }
    loop(stopTimes.toList, List())
  }

  private def transfersToConnections(gtfsData: GtfsData): Iterable[Connection] = {
    val filteredTransfers: Iterable[Transfer] = gtfsData.transfers.filter(t =>
      // Keep only transfers from and to known stations
      gtfsData.stopsByStopId.contains(t.fromStopId) && gtfsData.stopsByStopId.contains(t.toStopId)
    )
    @tailrec
    def loop(transfers: Iterable[Transfer], connections: List[Connection]): List[Connection] = {
      transfers match {
        case Nil => connections
        case head :: Nil => connections
        case head :: tail =>
          val departureStop = head.fromStopId
          val arrivalStop = head.toStopId
          val transferConnections = gtfsData.stopTimesByStopId(departureStop).map(stopTime => {
            val connectionDepartureTime = durationToTimestamp(stopTime.arrivalTime)
            Connection(
              departureStop,
              arrivalStop,
              connectionDepartureTime,
              connectionDepartureTime + head.minTransferTime
            )
          })
          loop(tail, connections ++ transferConnections)
      }
    }
    loop(filteredTransfers.toList, List())
  }

  private def durationToTimestamp(duration: Duration) = {
    LocalDate.now().atTime(LocalTime.MIDNIGHT).plusSeconds(duration.getSeconds).toInstant(ZoneOffset.UTC).getEpochSecond.toInt
  }
}
