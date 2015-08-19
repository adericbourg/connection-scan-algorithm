package algo

case class Connection(departureStation: Int, arrivalStation: Int, departureTimestamp: Int, arrivalTimestamp: Int) {
  override def toString: String = {
    s"Departure: $departureStation@$departureTimestamp, Arrival: $arrivalStation@$arrivalTimestamp"
  }
}

object Connection {
  def parse(line: String): Connection = {
    val split: Array[String] = line.split(" ")
    Connection(
      split(0).toInt,
      split(1).toInt,
      split(2).toInt,
      split(3).toInt
    )
  }
}
