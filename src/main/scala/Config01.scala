import com.typesafe.config.{ConfigFactory, ConfigMemorySize}
import io.circe.config.syntax._
import io.circe.generic.auto._
import shapeless.{::, HList, HNil}

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

object Config01 extends App {

  case class ServerSettings(host: String, port: Int, timeout: FiniteDuration, maxUpload: ConfigMemorySize)
  case class HttpSettings(server: ServerSettings, version: Option[Double])
  case class AppSettings(http: HttpSettings)

  case class Test(x: String, i: Int)

  val config = ConfigFactory.parseString(
    """
      |http {
      |  version = 1.1
      |  server {
      |    host = localhost
      |    port = 8080
      |    timeout = 5 s
      |    maxUpload = 5M
      |  }
      |}
    """.stripMargin)

  println(config.as[AppSettings])

  val hl1 = Try("x") :: Try(throw new Exception("")) :: HNil
  val hl2: HList = Try("x") :: Try(1) :: HNil


  def validate(hl: HList, errs: List[Throwable] = List(), data: HList = HNil): Either[List[Throwable], HList] =
    hl match {
      case HNil               => errs match { case Nil => Right(data); case _ => Left(errs) }
      case Success(s) :: rest => validate(rest, errs, s :: data)
      case Failure(f) :: rest => validate(rest, f :: errs, data)
      case x :: rest          => validate(rest, errs, x :: data)
  }

  def reverse(hl : HList, res: HList = HNil): HList = hl match {
    case HNil    => res
    case x :: xs => reverse(xs, x :: res)
  }

  println(validate(hl1))
  println(reverse(validate(hl2).getOrElse("" :: -1 :: HNil)))

  val xx: HList = reverse(validate(hl2).getOrElse("x" :: 1 :: HNil))


}
