package parser.ratp

import java.io.File

import com.github.tototoshi.csv._

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
      stopsByStopId.getOrElse(stopTime.stopId, List()).foreach(stop => println(s"${stop.stopName} : ${stopTime.arrivalTime} : ${stopTime.departureTime}"))
    )
  }
}
