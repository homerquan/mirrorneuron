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
import io.convospot.engine.constants.Timeouts

private[convospot] object GrpcExecutor {
  private implicit val system = ActorSystem("convospot-engine")
  private implicit val shortTimeout = Timeout(Timeouts.SHORT)

  def handlers(req: Request): Future[Response] = {
    req.`type` match {
      case "create_bot" => {
        createBot(req)
      }
      case "create_conversation" => {
        createConversation(req)
      }
      case "create_visitor" => {
        createVisitor(req)
      }
      case "create_helper" => {
        createHelper(req)
      }
      case "join_conversation" => {
        joinConversation(req)
      }
      case "supervise_conversation" => {
        superviseConversation(req)
      }
      case "leave_conversation" => {
        leaveConversation(req)
      }
      case "unsupervise_conversation"=> {
        unsuperviseConversation(req)
      }
      case "say" => {
        say(req)
      }
      case "switch_conversation_mode" => {
        switchConversationMode(req)
      }
      case "end_conversation" => {
        createBot(req)
      }
      case "reset_engine" => {
        createBot(req)
      }
      case _ => {
        val ex = new RuntimeException("unsupported request topic:" + req.`type`)
        Future.failed(ex)
      }
    }
  }

  private def createBot(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateBot]
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = s"Bot ${data.id} created success!")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def createVisitor(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateVisitor]
      var botActor = Await.result(system.actorSelection("/user/"+data.bot).resolveOne(), shortTimeout.duration)
      botActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def createHelper(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateHelper]
      var botActor = Await.result(system.actorSelection("/user/"+data.bot).resolveOne(), shortTimeout.duration)
      botActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def createConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[CreateConversation]
      var botActor = Await.result(system.actorSelection("/user/"+data.bot).resolveOne(), shortTimeout.duration)
      botActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def joinConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[JoinConversation]
      var visitorActor = Await.result(system.actorSelection("/user/"+data.bot+"/"+data.visitor).resolveOne(), shortTimeout.duration)
      visitorActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def leaveConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[LeaveConversation]
      var visitorActor = Await.result(system.actorSelection("/user/"+data.bot+"/"+data.visitor).resolveOne(), shortTimeout.duration)
      visitorActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def superviseConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[SuperviseConversation]
      var helperActor = Await.result(system.actorSelection("/user/"+data.bot+"/"+data.user).resolveOne(), shortTimeout.duration)
      helperActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def unsuperviseConversation(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[UnsuperviseConversation]
      var helperActor = Await.result(system.actorSelection("/user/"+data.bot+"/"+data.user).resolveOne(), shortTimeout.duration)
      helperActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def say(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[Say]
      var visitorActor = Await.result(system.actorSelection("/user/"+data.bot+"/"+data.sid).resolveOne(), shortTimeout.duration)
      visitorActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def switchConversationMode(req: Request) = {
    try {
      val data = req.data.parseJson.convertTo[SwitchConversationMode]
      var conversationActor = Await.result(system.actorSelection("/user/"+data.bot+"/"+data.conversation).resolveOne(), shortTimeout.duration)
      conversationActor ! data
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

  private def act(req: Request) = {
    val data = req.data.parseJson.convertTo[CreateBot]
    try {
      system.actorOf(Props(new BotActor()), data.id)
      val reply = Response(message = "ok")
      Future.successful(reply)
    } catch {
      case e: Exception => Future.failed(e)
    }
  }

}
