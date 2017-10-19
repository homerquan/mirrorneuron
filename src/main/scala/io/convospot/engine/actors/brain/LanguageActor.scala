package io.convospot.engine.actors.brain

import akka.actor._
import akka.util.Timeout
import io.convospot.engine.actors.conversation.RoomActor

import scala.concurrent.Await

class LanguageActor extends Actor with ActorLogging{

  def receive = active(scala.collection.mutable.Map.empty[String,String])
  def active(memory:scala.collection.mutable.Map[String,String]):Receive = {
    case LanguageActor.Message.Ask(message:String) =>
      val reply=memory.getOrElse(message,"[Stupid AI does not know]")
      log.info("memory is {}",memory.toString())
      sender ! PolicyActor.Message.LanguageReply(reply)
    case LanguageActor.Message.Learn(conversation:String, reply:String)=>
      context become active(memory += (conversation->reply))
    case _ => println("that was unexpected")
  }

}

object LanguageActor {

  object Command {

  }

  object Message {
    final case class Ask(message: String)
    final case class Learn(conversation: String, reply: String)
  }
}