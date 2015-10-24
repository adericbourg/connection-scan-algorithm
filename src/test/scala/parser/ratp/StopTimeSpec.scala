package parser.ratp

import java.time.Duration

import org.specs2.mutable.Specification


class StopTimeSpec extends Specification {

  "StopTime parser" should {
    "parse all fields" in {
      val stopTime: StopTime = StopTime.parse(Map(
        "trip_id" -> "1",
        "arrival_time" -> "01:02:03",
        "departure_time" -> "04:05:06",
        "stop_id" -> "2"
      ))

      stopTime.tripId mustEqual 1L
      stopTime.arrivalTime mustEqual Duration.ofHours(1).plusMinutes(2).plusSeconds(3)
      stopTime.departureTime mustEqual Duration.ofHours(4).plusMinutes(5).plusSeconds(6)
      stopTime.stopId mustEqual 2L
    }
  }



  "StopTime parser" should {
    "handle time after 24th hour" in {
      val stopTime: StopTime = StopTime.parse(Map(
        "trip_id" -> "1",
        "arrival_time" -> "23:59:59",
        "departure_time" -> "25:00:01",
        "stop_id" -> "1"
      ))

      stopTime.arrivalTime mustEqual Duration.ofHours(23).plusMinutes(59).plusSeconds(59)
      stopTime.departureTime mustEqual Duration.ofDays(1).plusHours(1).plusSeconds(1)
    }
  }
}
