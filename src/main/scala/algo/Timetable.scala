package algo

case class Timetable(connections: Seq[Connection])

object Timetable {
  def parse(lines: Iterator[String]): Timetable = {
    Timetable(lines.
      map(Connection.parse).
      toList
    )
  }
}
