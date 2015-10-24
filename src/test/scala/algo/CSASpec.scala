package algo

import org.specs2.mutable.Specification

class CSASpec extends Specification {

  "CSA" should {
    "find a route when connections are available" in {
      val departureStation: Int = 1
      val arrivalStation: Int = 4
      val departureTime: Int = 0

      val timetable = Timetable(Seq(
        Connection(departureStation, 2, 1, 1),
        Connection(2, 3, 2, 2),
        Connection(3, arrivalStation, 3, 3)
      ))

      val route = CSA(timetable).compute(departureStation, arrivalStation, departureTime)

      route.length mustEqual 3

      route.head.departureStation mustEqual departureStation
      route.head.departureTimestamp must beGreaterThanOrEqualTo(departureTime)
      route.last.arrivalStation mustEqual arrivalStation
    }
  }

  "CSA" should {
    "not find a route when no connections are available" in {
      val departureStation: Int = 1
      val arrivalStation: Int = 4

      val timetable = Timetable(Seq(
        Connection(1, 2, 1, 1),
        Connection(3, 4, 2, 2)
      ))

      val route = CSA(timetable).compute(departureStation, arrivalStation, 0)

      route must beEmpty
    }
  }

}
