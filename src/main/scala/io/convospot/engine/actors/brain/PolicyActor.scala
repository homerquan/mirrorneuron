package io.convospot.engine.actors.brain
import akka.actor._
import io.convospot.engine.actors.conversation.RoomActor

case object AskNameMessage

class PolicyActor extends Actor {
  def receive = {
    case AskNameMessage => // respond to the "ask" request
      sender ! "Fred"
    case PolicyActor.Message.Generic(message:String, sourceRole) =>  // Send two messages to the room
      if(sourceRole=="VISITOR") {
        
        sender ! RoomActor.Message.FromAI("[AI can not understand] " + message, "[visitor said] "+message)
      }  else {
        sender ! RoomActor.Message.FromAI(message, "[Received]")
      }
    case _ => println("that was unexpected")
  }

}

object PolicyActor {

  object Command {

  }

  object Message {

    final case class Generic(message: String, sourceRole: String)

  }
}