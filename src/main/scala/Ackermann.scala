
object Ackermann extends App {

  def ack(m: Int, n: Int): Int = {
    require( m >=0 && n >= 0 )
    (m, n) match {
      case (0, _) => n + 1
      case (_, 0) => ack(m - 1, 1)
      case (_, _) => ack(m - 1, ack(m, n - 1))
    }
  }

  def ackM(m: Int, n: Int): Int = {
    require( m >=0 && n >= 0 )
    def ackMem(m: Int, n: Int, mem: Map[(Int, Int), Int]): Int = {
      mem.getOrElse((m, n),
        {
          (m, n) match {
            case (0, _) => n + 1
            case (_, 0) => ackMem(m - 1, 1, mem)
            case (_, _) => ackMem(m - 1, ack(m, n - 1), mem)
          }
        })
    }
    val mns = for { m <- 0 to m; n <- 0 to n } yield (m, n)
    val mem = mns.foldLeft(Map[(Int, Int), Int]())((acc, mn) => acc + ((mn._1, mn._2) -> ackMem(mn._1, mn._2, acc)))
    mem.getOrElse((m, n), -1)
  }

  println(ackM(4, 2))
}
