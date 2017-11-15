package io.convospot.engine.grpc

import akka.actor.{ActorRef, ActorSystem, Props, InvalidActorNameException}
import io.convospot.engine.actors.context.BotActor
import io.convospot.engine.grpc.conversation.{Request, Response}
import io.convospot.engine.util.RedisConnector
import spray.json._
import io.convospot.engine.grpc.data.JsonProtocol._
import io.convospot.engine.grpc.data._


import scala.concurrent.Future

private[convospot] object Handlers {
  private implicit val system = ActorSystem("convospot-engine")
  val redis = RedisConnector.getRedis

  def createBot(req: Request) = {
    val data = req.data.parseJson.convertTo[CreateBot]
    try {
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot $data.id created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def createUser(req: Request) = {
    val data = req.data.parseJson.convertTo[CreateBot]
    try {
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot $data.id created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def createConversation(req: Request) = {
    val data = req.data.parseJson.convertTo[CreateBot]
    try {
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot $data.id created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

}
