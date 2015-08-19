package parser.ratp

import java.time.Duration

import org.scalatest.{FlatSpec, Matchers}

class StopTime$Test extends FlatSpec with Matchers {

  "parser" should "parse all fields" in {
    val stopTime: StopTime = StopTime.parse(Map(
      "trip_id" -> "1",
      "arrival_time" -> "01:02:03",
      "departure_time" -> "04:05:06",
      "stop_id" -> "2"
    ))

    stopTime.tripId should be (1L)
    stopTime.arrivalTime should be(Duration.ofHours(1).plusMinutes(2).plusSeconds(3))
    stopTime.departureTime should be(Duration.ofHours(4).plusMinutes(5).plusSeconds(6))
    stopTime.stopId should be (2L)
  }

  "parser" should "handle time after 24th hour" in {
    val stopTime: StopTime = StopTime.parse(Map(
      "trip_id" -> "1",
      "arrival_time" -> "23:59:59",
      "departure_time" -> "25:00:01",
      "stop_id" -> "1"
    ))

    stopTime.arrivalTime should be(Duration.ofHours(23).plusMinutes(59).plusSeconds(59))
    stopTime.departureTime should be(Duration.ofDays(1).plusHours(1).plusSeconds(1))
  }
}
