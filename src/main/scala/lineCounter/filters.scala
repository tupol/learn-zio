package lineCounter

object filters {

  val Brackets = "{[()]}"

  val FilterNone = (value: String) => true
  val JustBrackets = (value: String) => value.trim.filterNot(Brackets.contains(_)).size == 0
  val Comment = (value: String) => {
    val trimmed = value.trim
    trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("#")
  }
  val CustomFilter = (value: String) => JustBrackets(value) || Comment(value)

}
