

object Typeclasses extends App {

  trait Renderer[T] {
    def render(x: T): String
  }
  object Renderer {
    def apply[T](implicit renderer: Renderer[T]) = renderer

    implicit val DoubleRenderer = new Renderer[Double]{
      def render(x: Double) = f"(double: $x%.2f)"
    }

    implicit val StringRenderer = new Renderer[String]{
      def render(x: String) = f"(string: $x)"
    }

    implicit def ListRenderer[T](implicit renderer: Renderer[T]) = new Renderer[Seq[T]]{
      def render(x: Seq[T]) = "[" + x.map(renderer.render).mkString(", ") + "]"
    }
  }

  implicit class RenderOps[T](val v: T) extends AnyVal {
    def render(implicit ev: Renderer[T]): String = ev.render(v)
  }


  println(Renderer[Double].render(1.3))
  println(1.3.render)
  println(Seq(1.3, 1.2).render)

}
