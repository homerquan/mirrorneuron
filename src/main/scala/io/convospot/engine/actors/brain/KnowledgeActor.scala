package io.convospot.engine.actors.brain
import akka.actor.{Actor, ActorLogging}
import io.convospot.engine.util.RedisConnector
import spray.json._

import com.softwaremill.sttp._


class KnowledgeActor extends Actor with ActorLogging{
  val redis=RedisConnector.getRedis
  val redisPool= RedisConnector.getPool
  //demo only
  var kb = redis.hget("demo","knowledge").get
  def receive = {
    case KnowledgeActor.Message.Ask(message: String) =>
        //ask Machine Comprehension api
        kb = redis.hget("demo","knowledge").get
        sender ! getAnswer(kb,message)
    case KnowledgeActor.Message.Learn(message: String) =>
        redis.hset("demo","knowledge",kb+"\n"+message)
    case _ => println("that was unexpected")
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

  }

  object Message {

    final case class Ask(message: String)
    final case class Learn(reply: String)

  }

}