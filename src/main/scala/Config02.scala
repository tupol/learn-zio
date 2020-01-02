import com.typesafe.config.{ConfigFactory, ConfigMemorySize}
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

object Config02 extends App {

  case class ServerSettings(host: String, port: Int, timeout: FiniteDuration, maxUpload: ConfigMemorySize)
  case class HttpSettings(server: ServerSettings, version: Option[Double])
  case class AppSettings(http: HttpSettings)

  val configStr =
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
    """.stripMargin
  val config = ConfigFactory.parseString(configStr)
//  println(ConfigSource.string(configStr).load[AppSettings])

}
