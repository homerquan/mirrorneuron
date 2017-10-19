package io.convospot.engine.actors.brain
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import io.convospot.engine.actors.conversation.RoomActor

import scala.concurrent.Await

case object AskNameMessage

class PolicyActor extends Actor {

  val languageActor = context.actorOf(Props[LanguageActor],"sample_language_actor")

  def receive = {
    case AskNameMessage => // respond to the "ask" request
      sender ! "Fred"
    case PolicyActor.Message.InFromRoom(message:String, sourceRole:String, conversation:String) =>  // Send two messages to the room
      if(sourceRole=="VISITOR") {
        //TODO: only trying to understand language here, it should go KB and Action later
        implicit val timeout = Timeout(15 seconds)
        val future = languageActor ? LanguageActor.Message.Ask(message)
        val result = Await.result(future, timeout.duration).asInstanceOf[PolicyActor.Message.LanguageReply]
        sender ! RoomActor.Message.FromAI(result.message, message)
      }  else {
        languageActor ! LanguageActor.Message.Learn(conversation,message)
        sender ! RoomActor.Message.FromAI(message, "[Learned]")
      }
    case _ => println("that was unexpected")
  }

}

object PolicyActor {

  object Command {

  }

  object Message {
    final case class InFromRoom(message: String, sourceRole: String, conversation: String)
    final case class LanguageReply(message: String)
  }
}