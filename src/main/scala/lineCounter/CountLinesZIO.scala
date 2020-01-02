package lineCounter

import java.io.{BufferedReader, File, FileReader}
import java.nio.file.{Path, Paths}

import zio._
import zio.console.putStrLn

object CountLinesZIO extends App {

  def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
      mainLogic.fold(_ => -1, _ => 0)

  def zreader(file: File): ZManaged[Any, Throwable, BufferedReader] =
    ZManaged.make(Task(new BufferedReader(new FileReader(file))))(r => UIO(r.close()))

  def zroot(path: String): Task[Path] = Task(Paths.get(path))

  def zfiles(path: Path): Task[Iterator[File]] = Task(files(path))

  def linesPerFile(files: Iterator[File]): Iterator[Task[(File, Long)]] =
    files.map{ f => zreader(f).use(reader => Task((f, countLines(reader)))) }

  val mainLogic = for {
      startTime         <- Task.succeed(System.currentTimeMillis())
      rootPath          <- zroot("/Users/olivertupran/work/tupol/")
      fcx               <- zfiles(rootPath).map(files => linesPerFile(files).toSeq)
      files             <- zxfiles(rootPath.toFile)
      fcxx              = linesPerFile(files.toIterator).toIterable
      linesPerFile      <- Task.collectAllPar(fcxx)
      linesPerExtension <- Task {
                            linesPerFile
                                .map{ case (file, lines) => (extension(file), lines)}
                                .groupBy(_._1).toSeq
                                .map{ case (ext, lpf) => (ext, lpf.size, lpf.map(_._2).reduce(_ + _))}
                            }
      _                 <- ZIO.collectAll{
                            linesPerExtension.sortBy(_._3).reverse
                              .map { case(f, s, c) => putStrLn(f"$f%-32s : $s% 9d : $c% 9d") }
                            }
      endTime           <- Task.succeed(System.currentTimeMillis())
      _                 <- putStrLn(f"TOTAL FILES   : ${linesPerExtension.map(_._2).reduce(_ + _)}% 9d")
      _                 <- putStrLn(f"TOTAL LINES   : ${linesPerExtension.map(_._3).reduce(_ + _)}% 9d")
      _                 <- putStrLn(f"TOTAL RUNTIME : ${(endTime-startTime)/1000.0}%9.2f")
    } yield (linesPerExtension)


  def zxfiles(root: File, extensions: Seq[String] = Extensions): Task[Seq[File]] = root.isFile match {
    case true  => if(extensions.contains(extension(root))) Task.succeed(Seq(root)) else Task.succeed(Seq())
    case false =>
      Task.collectAllParN(4)(root.listFiles().map(f => Task(zxfiles(f, extensions))))
        .flatMap{ x => Task.collectAllParN(4)(x).map(_.flatten) }
  }


  def zxfiles2(input: Seq[File], result: Task[Seq[File]], extensions: Seq[String] = Extensions): Task[Seq[File]] = input match {
    case Nil => result
    case _   =>
    val dirs = input.filter(_.isDirectory)
    val files = Task.succeed(input.filter(f => f.isFile && extensions.contains(extension(f))))
    val next = dirs.flatMap(_.listFiles())
    val acc = for {
      prev <- result
      curr <- files
    } yield (prev ++ curr)
    zxfiles2(next, acc, extensions)
  }

  def zxfiles3(input: Seq[File], result: Seq[Task[File]], extensions: Seq[String] = Extensions): Task[Seq[File]] = input match {
    case Nil => Task.collectAllPar(result)
    case _   =>
      val dirs = input.filter(_.isDirectory)
      val files: Seq[Task[File]] = input.filter(f => f.isFile && extensions.contains(extension(f))).map(Task.succeed)
      val next = dirs.flatMap(_.listFiles())
      zxfiles3(next, files ++ result, extensions)
  }

}
