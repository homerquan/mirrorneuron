import spray.json._

val source = """{ "some": "JSON source" }"""
val jsonAst = source.parseJson // or JsonParser(source)