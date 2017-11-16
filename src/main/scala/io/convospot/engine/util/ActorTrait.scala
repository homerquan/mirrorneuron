package io.convospot.engine.util
import akka.actor.{ActorContext, ActorRef}

trait ActorTrait {
  def findChildByName(name: String, context: ActorContext): Option[ActorRef] = context.child(name)
}

