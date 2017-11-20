package io.convospot.engine.actors.brain

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}

/**
  * Visitor. One for each connection.
  * Demonstrates context.become FSM solution
  * context is representing the observed behavior
  */
class UserActor(bot: ActorContext) extends Actor with ActorLogging {

  def receive = {
    case _ =>
  }

}

object UserActor {

  def props(bot: ActorContext) = Props(new UserActor(bot))

}