package io.convospot.engine.actors.context

import akka.actor.{Actor, ActorLogging}
import io.convospot.engine.util.ActorTrait

/**
  * Call GRPC in nodejs
  */
private[convospot] class BotOutputActor(botId:String) extends Actor with ActorTrait with ActorLogging {

  def receive = {
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }

}
