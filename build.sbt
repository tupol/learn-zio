

name := "learn-zio"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "dev.zio" %% "zio" % Versions.zio
libraryDependencies += "io.circe" %% "circe-config" % Versions.circe_config
libraryDependencies += "io.circe" %% "circe-generic" % Versions.circe
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig


val Versions = new {
  val zio = "1.0.0-RC15"
  val circe = "0.12.2"
  val circe_config = "0.7.0"
  val pureconfig = "0.12.1"
}
