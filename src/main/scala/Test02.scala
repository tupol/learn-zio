import zio.console._
import zio.{App, Task, ZIO}

object Test02 extends App {

  type Throwables = Seq[Throwable]

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
      x <- myAppLogic.fold(_ => -1, _ => 0)
      _ <- putStr(s"x = $x")
    } yield(x)

  def somethingThatFails1(i: Int): Int = {
    println(s"somethingThatFails1($i)")
    if(i < 1) throw new IllegalAccessException("1") else i
  }
  def somethingThatFails2(i: Int): Int = {
    println(s"somethingThatFails2($i)")
    if(i < 2) throw new IllegalAccessException("2") else i
  }

  val myAppLogic: ZIO[Any, Throwable, Unit] =
    for {
      a0    <- Task.succeed(1)
      a1    <- Task(somethingThatFails1(a0))
      a2    <- Task(somethingThatFails2(a1))
    } yield (a2)
}
