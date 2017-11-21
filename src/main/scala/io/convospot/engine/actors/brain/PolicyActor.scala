package io.convospot.engine.actors.brain

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import io.convospot.engine.actors.common.Messages
import scala.concurrent.Await

case object AskNameMessage

private[convospot] class PolicyActor(conversation:ActorContext) extends Actor with ActorLogging{

  val languageActor = context.actorOf(Props[LanguageActor], "sample_language_actor")
  val knowledgeActor = context.actorOf(Props[KnowledgeActor], "sample_knowledge_actor")

  def receive = {
    case AskNameMessage => // respond to the "ask" request
      sender ! "Fred"
    case PolicyActor.Message.InFromRoom(message: String, sourceRole: String, conversation: String) => // Send two messages to the room
      if (sourceRole == "VISITOR") {
        //TODO: only trying to understand language here, it should go KB and Action later
        implicit val timeout = Timeout(15 seconds)
        val future = languageActor ? LanguageActor.Message.Ask(message)
        val result = Await.result(future, timeout.duration)
        val future2 = knowledgeActor ? KnowledgeActor.Message.Ask(message)
        val result2 = Await.result(future2, timeout.duration)

        // wait human to select within a timeout

        log.debug(result2.asInstanceOf[String])
        if (result2.isInstanceOf[String])
          sender ! Messages.OutputAI(Messages.Acknowledge("AI Response:"+message), Messages.Utterance(result2.asInstanceOf[String],"AI","VISITOR"))
        if (result.isInstanceOf[Messages.Utterance])
          sender ! Messages.OutputAI(Messages.Acknowledge("AI Response:"+message), result.asInstanceOf[Messages.Utterance])
        if (result.isInstanceOf[Messages.Acknowledge])
          sender ! Messages.OutputAI(Messages.Acknowledge("AI fail, you need to answer"), result.asInstanceOf[Messages.Acknowledge])
      } else {
        languageActor ! LanguageActor.Message.Learn(conversation, message)
        sender ! Messages.OutputAI(Messages.Acknowledge("learned"), Messages.Utterance(message,"HELPER","VISITOR"))
      }
    case _ => println("that was unexpected")
  }

}

object PolicyActor {

  object Command {

  }

  object Message {

    final case class InFromRoom(message: String, sourceRole: String, conversation: String)

    final case class Learn(context: String)

  }

}