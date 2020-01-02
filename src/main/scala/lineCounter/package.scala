import java.io.{BufferedReader, File, FileReader}
import java.nio.file.{Files, Path}

import lineCounter.filters.CustomFilter

import scala.jdk.StreamConverters._


package object lineCounter {

  def countLines(reader: BufferedReader): Long = reader.lines.toScala(Iterator).count(!CustomFilter(_))

  def reader(file: File) = new BufferedReader(new FileReader(file))

  def extension(file: File) = {
    val name = file.getName
    val ext = name.substring(name.lastIndexOf(".") + 1)
    if(ext.size == name.size) "N/A" else ext
  }

  val Extensions = Seq("scala", "java", "md", "python", "xml", "properties", "N/A", "sh", "bat", "json", "class")

  def files(root: Path, extensions: Seq[String] = Extensions) =
    Files.walk(root).toScala(Iterator).map(_.toFile)
    .filter(f => f.isFile && { val ex = extension(f); extensions.contains(ex) } )

  def xfiles(root: File, extensions: Seq[String] = Extensions): Seq[File] = root.isFile match {
    case true  => if(extensions.contains(extension(root))) Seq(root) else Seq()
    case false => root.listFiles().map(xfiles(_, extensions)).reduce(_ ++ _)
  }

  def xfiles2(input: Seq[File], result: Seq[File] = Seq(), extensions: Seq[String] = Extensions): Seq[File] = input match {
    case Nil => result
    case _   =>
      val dirs = input.filter(_.isDirectory)
      val files = input.filter(f => f.isFile && extensions.contains(extension(f)))
      val next = dirs.flatMap(_.listFiles())
      xfiles2(next, files ++ result, extensions)
  }

  def timeCode[T](code: => T): (T, Long) = {
    val start = System.currentTimeMillis()
    val result = code
    val runtimeMillis = System.currentTimeMillis() - start
    (result, runtimeMillis)
  }

}
