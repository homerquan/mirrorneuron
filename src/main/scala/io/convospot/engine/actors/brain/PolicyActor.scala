package io.convospot.engine.actors.brain
import akka.actor._

case object AskNameMessage

class PolicyActor extends Actor {
  def receive = {
    case AskNameMessage => // respond to the "ask" request
      sender ! "Fred"
    case _ => println("that was unexpected")
  }
}

