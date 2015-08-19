package parser.ratp

case class Transfer(fromStopId: Long, toStopId: Long, transferType: String, minTransferTime: Int)

object Transfer {
  def parse(fields: Map[String, String]) = {
    Transfer(
      fields("from_stop_id").toLong,
      fields("to_stop_id").toLong,
      fields("transfer_type"),
      fields("min_transfer_time").toInt
    )
  }
}