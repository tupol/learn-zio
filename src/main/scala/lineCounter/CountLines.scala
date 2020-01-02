package lineCounter

import java.nio.file.Paths

object CountLines {

  def main(args: Array[String]) = {
//    val root = Paths.get(if(args.isEmpty) "" else args(0))
    val start = System.currentTimeMillis()
    val root = Paths.get("/Users/olivertupran/work/tupol/spark")

    val (linesPerFile, rt1) = timeCode(xfiles2(Seq(root.toFile))
      .map { f =>
        val rx = reader(f)
        val count = countLines(rx)
        rx.close()
        (f, count) }
      .toSeq)

    val (linesPerExtension, rt3) = timeCode(linesPerFile
        .map{ case (file, lines) => (extension(file), lines)}
        .groupBy(_._1).toSeq
        .map{ case (ext, lpf) => (ext, lpf.size, lpf.map(_._2).reduce(_ + _))})

    linesPerExtension.sortBy(_._3).reverse.foreach{ case(f, s, c) => println(f"$f%-32s : $s% 9d : $c% 9d") }

    println(f"TOTAL FILES   : ${linesPerExtension.map(_._2).reduce(_ + _)}% 9d")
    println(f"TOTAL LINES   : ${linesPerExtension.map(_._3).reduce(_ + _)}% 9d")

    val end = System.currentTimeMillis()
    println((f"TOTAL RUNTIME : ${(end-start)/1000.0}%9.2f"))
  }


}
