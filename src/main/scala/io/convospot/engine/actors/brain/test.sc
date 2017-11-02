import com.softwaremill.sttp._
import spray.json._

val json =
  """
    |{"passage":"Convospot is a live communication service for education. It uses augmented​ intelligence to help understand and engage on-line learners. It provides an “autopilot system” to enhance teachers' judgment and improve their responses.\nIt is a channel to serve people’s knowledge with assistance of machine, and a platform to accumulate, share and manage knowledge.\n\n","question":"what is convospot"}
  """.stripMargin
val request = sttp.post(uri"http://10.0.1.100:8008/predict/machine-comprehension")
implicit val backend = HttpURLConnectionBackend()
val response = request.body(json).send()

// response.header(...): Option[String]
println(response.header("Content-Length"))

// response.unsafeBody: by default read into a String
response.unsafeBody.parseJson.asJsObject.getFields("best_span_str")(0).toString()