package io.convospot.engine.actors.brain
import akka.actor._
import io.convospot.engine.actors.conversation.RoomActor

case object AskNameMessage

class PolicyActor extends Actor {
  def receive = {
    case AskNameMessage => // respond to the "ask" request
      sender ! "Fred"
    case PolicyActor.Message.Generic(message:String) =>  // Send two messages to the room
      sender ! RoomActor.Message.FromAI("you said:" + message, "I don't understand:" + message)
    case _ => println("that was unexpected")
  }

}

object PolicyActor {

  object Command {

  }

  object Message {

    final case class Generic(message: String)

  }
}