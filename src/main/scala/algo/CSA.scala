package algo

import scala.annotation.tailrec
import scala.io.Source

/**
 * FIXME Rewrite this in a more functionnal way.
 */
case class CSA(timetable: Timetable) {
  val inConnection = Array.fill[Int](CSA.MaxStations)(Int.MaxValue)
  val earliestArrival = Array.fill[Int](CSA.MaxStations)(Int.MaxValue)

  private def loop(arrivalStation: Int): Unit = {
    @tailrec
    def inner(conns: Seq[(Connection, Int)], earliest: Int): Unit = {
      var newEarliest = earliest
      conns match {
        case Seq() =>
          ()
        case (connection, index) +: _ if connection.arrivalTimestamp > earliest =>
          ()
        case (connection, index) +: tail =>
          if (leavesAfterArrival(connection) && optimizesArrivalTime(connection)) {
            earliestArrival(connection.arrivalStation) = connection.arrivalTimestamp
            inConnection(connection.arrivalStation) = index
            if (connection.arrivalStation == arrivalStation) {
              newEarliest = Math.min(earliest, connection.arrivalTimestamp)
            }
          }
          inner(tail, newEarliest)
      }
    }
    inner(timetable.connections.zipWithIndex, Int.MaxValue)
  }

  def leavesAfterArrival(connection: Connection): Boolean = {
    connection.departureTimestamp >= earliestArrival(connection.departureStation)
  }

  def optimizesArrivalTime(connection: Connection): Boolean = {
    connection.arrivalTimestamp < earliestArrival(connection.arrivalStation)
  }

  private def printResult(arrivalStation: Int): Unit = {
    inConnection(arrivalStation) match {
      case Int.MaxValue => println("No solution")
      case _ => {
        var route = Array[Connection]()
        var lastConnectionIndex = inConnection(arrivalStation)
        while (lastConnectionIndex != Int.MaxValue) {
          val connection: Connection = timetable.connections(lastConnectionIndex)
          route = route :+ connection
          lastConnectionIndex = inConnection(connection.departureStation)
        }
        route.reverse.foreach { connection =>
          println(connection.toString)
        }
      }
    }
  }

  private def computeRoute(arrivalStation: Int): Seq[Connection] = {
    inConnection(arrivalStation) match {
      case Int.MaxValue =>
        println("No solution")
        Seq()
      case _ => {
        var route = Array[Connection]()
        var lastConnectionIndex = inConnection(arrivalStation)
        while (lastConnectionIndex != Int.MaxValue) {
          val connection: Connection = timetable.connections(lastConnectionIndex)
          route = route :+ connection
          lastConnectionIndex = inConnection(connection.departureStation)
        }
        println(s"Solution found with ${route.length} connections")
        route.reverse
      }
    }
  }

  def compute(departureStation: Int, arrivalStation: Int, departureTime: Int): Seq[Connection] = {
    earliestArrival(departureStation) = departureTime

    if (departureStation <= CSA.MaxStations && arrivalStation <= CSA.MaxStations) {
      loop(arrivalStation)
    }

    val route = computeRoute(arrivalStation)
    route.foreach(connection => println(connection.toString))
    route
  }
}

object CSA {
  val MaxStations = 5000000

  def main(args: Array[String]) {
    val timetable = Timetable.parse(Source.fromFile("src/main/resources/bench_data_48h").getLines())
    val csa: CSA = CSA(timetable)
    csa.compute(29377, 18650, 0)
  }
}
