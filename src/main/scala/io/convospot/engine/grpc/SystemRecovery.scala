package io.convospot.engine.grpc

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import io.convospot.engine.actors.context.BotActor
import io.convospot.engine.constants.GrpcOutputCode._
import io.convospot.engine.constants.Timeouts
import io.convospot.engine.grpc.data.JsonProtocol._
import io.convospot.engine.grpc.data._
import io.convospot.engine.grpc.output.{CommandsGrpc, Request, Response}
import io.convospot.engine.util.{ActorDebugTrait, CommonTrait}
import io.grpc.ManagedChannelBuilder
import spray.json._

import scala.concurrent.Await

private[convospot] class SystemRecovery(system:ActorSystem) extends CommonTrait with ActorDebugTrait{
  private implicit val shortTimeout = Timeout(Timeouts.SHORT)
  private implicit val apiGrpcHost = config.getString("grpc.api-host")
  private implicit val apiGrpcPort = config.getInt("grpc.api-port")
  private implicit val channel = ManagedChannelBuilder.forAddress(apiGrpcHost, apiGrpcPort).usePlaintext(true).build

  def restore() = {
    log.debug("Start to restore actor system...")
    this.restoreBot()
    this.restoreVisitor()
    this.restoreConversation()
    printActors(system)

  }

  private def restoreBot() = {
    val request = Request(typeCode = LIST_BOTS)
    val blockingStub = CommandsGrpc.blockingStub(channel)
    val reply: Response = blockingStub.ask(request)
    val bots = reply.data.parseJson.convertTo[List[Bot]]
    bots.map(bot =>
      system.actorOf(Props(new BotActor()), bot.id)
    )
  }

  private def restoreVisitor() = {
    val request = Request(typeCode = LIST_VISITORS)
    val blockingStub = CommandsGrpc.blockingStub(channel)
    val reply: Response = blockingStub.ask(request)
    val visitors = reply.data.parseJson.convertTo[List[Visitor]]
    visitors.map(visitor => {
      var botActor = Await.result(system.actorSelection("/user/" + visitor.bot).resolveOne(), shortTimeout.duration)
      botActor ! CreateVisitor(visitor.id,visitor.bot,visitor.client)
    })
  }

  private def restoreConversation() = {
    val request = Request(typeCode = LIST_CONVERSATIONS)
    val blockingStub = CommandsGrpc.blockingStub(channel)
    val reply: Response = blockingStub.ask(request)
    val conversations = reply.data.parseJson.convertTo[List[Conversation]]
    conversations.map(conversation => {
      var botActor = Await.result(system.actorSelection("/user/" + conversation.bot).resolveOne(), shortTimeout.duration)
      botActor ! CreateConversation(conversation.id,conversation.bot,conversation.client)
      var visitorActor = Await.result(system.actorSelection("/user/"+conversation.bot+"/"+conversation.visitor).resolveOne(), shortTimeout.duration)
      visitorActor ! JoinConversation(conversation.visitor,conversation.id,conversation.bot,conversation.client)
    })
  }
}
