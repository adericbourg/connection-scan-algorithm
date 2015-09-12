package parser.ratp

import java.io.File
import java.time.{LocalDate, LocalTime, ZoneOffset}

import algo.{CSA, Connection, Timetable}
import com.github.tototoshi.csv._

import scala.annotation.tailrec

object Parser {
  def main(args: Array[String]) {
    val routes = CSVReader.open(new File("/home/alban/tmp/lines/ligne9/routes.txt")).allWithHeaders().map(Route.parse)
    val trips = CSVReader.open(new File("/home/alban/tmp/lines/ligne9/trips.txt")).allWithHeaders().map(Trip.parse)
    val stops = CSVReader.open(new File("/home/alban/tmp/lines/ligne9/stops.txt")).allWithHeaders().map(Stop.parse)
    val stopTimes = CSVReader.open(new File("/home/alban/tmp/lines/ligne9/stop_times.txt")).allWithHeaders().map(StopTime.parse)
    val transfers = CSVReader.open(new File("/home/alban/tmp/lines/ligne9/transfers.txt")).allWithHeaders().map(Transfer.parse)

    val stopTimesByTripId: Map[Long, List[StopTime]] = stopTimes.groupBy(_.tripId)
    val stopsByStopId: Map[Long, List[Stop]] = stops.groupBy(_.stopId)



    // Display some random trip
    val trip: Trip = trips.head
    stopTimesByTripId.get(trip.tripId).get.foreach(stopTime =>
      stopsByStopId.getOrElse(stopTime.stopId, List()).foreach(stop => println(s"${stop.stopName} (${stop.stopId}) : ${stopTime.arrivalTime} : ${stopTime.departureTime}"))
    )

    // Display connections for that trip
    val connections: Seq[Connection] = stopTimesToConnections(stopTimesByTripId.get(trip.tripId).get)
    connections.foreach(connection =>
      println(s"${connection.departureStation} (@${connection.departureTimestamp})-> ${connection.arrivalStation} (@${connection.arrivalTimestamp})")
    )

    // Build timetable... and go from Miromesnil to Grands Boulevards
    val timetable: Timetable = Timetable(connections)
    CSA(timetable).compute(1819, 1712, 1442054940)
  }

  private def stopTimesToConnections(stopTimes: Seq[StopTime]): Seq[Connection] = {
    @tailrec
    def loop(times: Seq[StopTime], connections: List[Connection]): List[Connection] = {
      times match {
        case Nil => connections
        case head :: Nil => connections
        case head :: tail =>
          val departureStop = head.stopId.toInt
          val arrivalStop = tail.head.stopId.toInt
          val departureTime = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusSeconds(head.departureTime.getSeconds).toInstant(ZoneOffset.UTC).getEpochSecond.toInt
          val arrivalTime = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusSeconds(tail.head.departureTime.getSeconds).toInstant(ZoneOffset.UTC).getEpochSecond.toInt
          val connection: Connection = Connection(departureStop, arrivalStop, departureTime, arrivalTime)
          loop(tail, connections :+ connection)
      }
    }
    loop(stopTimes, List())
  }
}
