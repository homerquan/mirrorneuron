package io.convospot.engine.actors.context

import akka.actor.{Actor, ActorContext, ActorLogging}

/**
  * Monitoring all conversation for the bot
  */
class ObserverActor(bot: ActorContext) extends Actor with ActorLogging {

  def receive = {
    case _ =>
  }

}