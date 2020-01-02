import zio.{App, ZIO}
import zio.console._

object Test01 extends App {

  def run(args: List[String]): ZIO[Console, Nothing, Int] =
    myAppLogic.fold(_ => 1, _ => 0)

  val myAppLogic =
    for {
      _    <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    } yield ()
}
