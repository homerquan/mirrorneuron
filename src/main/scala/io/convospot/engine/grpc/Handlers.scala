package io.convospot.engine.grpc
import akka.actor.{ActorRef, ActorSystem, Props}
import io.convospot.engine.actors.context.BotActor
import io.convospot.engine.grpc.conversation.{Request, Response}

import scala.concurrent.Future
private[convospot] object Handlers {
  private implicit val system = ActorSystem("engine")
  def createBot(req:Request) = {
    // parse json data and put Bot id in name
    val bot = system.actorOf(Props(new BotActor()), name="test")
    val reply = Response(message = "Success!")
    Future.successful(reply)
  }
}
