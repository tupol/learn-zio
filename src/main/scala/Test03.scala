import zio.console._
import zio.{App, Cause, Task, ZIO}

object Test03 extends App {

  val c: Cause[Nothing] = Cause.die(new IllegalArgumentException()) ++ Cause.die(new Exception())

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    for {
      _ <- putStrLn(s"c=c ${c.toString}")
      s <- Task.succeed(System.currentTimeMillis())
      x <- myAppLogic.fold(_ => -1, _ => 0)
      e <- Task.succeed(System.currentTimeMillis())
      _ <- putStrLn(s"x = $x")
      _ <- putStrLn(f"TOTAL RUNTIME : ${(e - s)/1000.0}%9.2f")
    } yield(x)

  def n: Task[Int] = Task {
    Thread.sleep(1000)
    1
  }

  val myAppLogic: ZIO[Console, Throwable, Int] =
    for {
      x <- Task.collectAllPar((0 until 100).map(_ => n))
      _ <- putStrLn(s"x = ${x.sum}")
    } yield x.sum
}
