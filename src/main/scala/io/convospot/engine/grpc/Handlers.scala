package io.convospot.engine.grpc

import akka.actor.{ActorRef, ActorSystem, InvalidActorNameException, Props}
import io.convospot.engine.actors.context.BotActor
import io.convospot.engine.grpc.conversation.{Request, Response}
import spray.json._
import io.convospot.engine.grpc.data.JsonProtocol._
import io.convospot.engine.grpc.data._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.util.Timeout

private[convospot] object Handlers {
  private implicit val system = ActorSystem("convospot-engine")
  private implicit val shortTimeout = Timeout(5 seconds)

  def createBot(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateBot]
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot ${data.id} created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def createVisitor(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateBot]
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Visitor ${data.id} created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def createConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateConversation]
      var botActor = Await.result(system.actorSelection("/user/"+data.bot).resolveOne(), shortTimeout.duration)
      botActor ! data
      val reply = Response(message = "Received.")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def joinConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateBot]
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot $data.id created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def leaveConversation(req: Request) = {
    val data = req.data.parseJson.convertTo[CreateBot]
    try {
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot $data.id created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def say(req: Request) = {
    val data = req.data.parseJson.convertTo[CreateBot]
    try {
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = "Received words!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  def act(req: Request) = {
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
