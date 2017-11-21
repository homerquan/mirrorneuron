package io.convospot.engine.actors.brain
import akka.actor.{Actor, ActorContext, ActorLogging}
import io.convospot.engine.util.RedisConnector
import spray.json._
import com.softwaremill.sttp._


private[convospot] class KnowledgeActor(bot:ActorContext) extends Actor with ActorLogging{
  val redis=RedisConnector.getRedis
  val redisPool= RedisConnector.getPool
  val key = bot.getClass.getSimpleName + "-kb"

  //var kb = redis.hget(key,"knowledge").get

  val kb = "convospot is a cool tool"

  def receive = {
    case KnowledgeActor.Command.Ask(message: String) =>
        sender ! PolicyActor.Command.AnswerFromKnowledge(getAnswer(kb,message))
    case KnowledgeActor.Command.Learn(message: String) =>
        //redis.hset(key,"knowledge",kb+"\n"+message)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }

  private def getAnswer(passage:String, question: String): String = {
    val json =
      s"""
        |{"passage":"$passage","question":"$question"}
      """.stripMargin
    val request = sttp.post(uri"http://10.0.1.100:8008/predict/machine-comprehension")
    implicit val backend = HttpURLConnectionBackend()
    val response = request.body(json).send()

    // response.header(...): Option[String]
    println(response.header("Content-Length"))

    // response.unsafeBody: by default read into a String
    response.unsafeBody.parseJson.asJsObject.getFields("best_span_str")(0).toString()

  }
}

object KnowledgeActor {

  object Command {
    final case class Ask(message: String)
    final case class Learn(reply: String)
  }

}