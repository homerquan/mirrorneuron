package io.convospot.engine.app

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import io.convospot.engine.actors.context.BotActor
import io.grpc.ManagedChannelBuilder
import io.convospot.engine.grpc.output.{CommandsGrpc, Request, Response}
import io.convospot.engine.util.{ActorDebugTrait, ObjectTrait}
import io.convospot.engine.constants.GrpcOutputCode._
import io.convospot.engine.constants.Timeouts
import io.convospot.engine.grpc.GrpcExecutor.{shortTimeout, system}
import spray.json._
import io.convospot.engine.grpc.data.JsonProtocol._
import io.convospot.engine.grpc.data._

import scala.concurrent.Await

private[convospot] object SystemRecovery extends ObjectTrait with LazyLogging with ActorDebugTrait{
  private implicit val system = ActorSystem("convospot-engine")
  private implicit val shortTimeout = Timeout(Timeouts.SHORT)
  private implicit val apiGrpcHost = config.getString("grpc.api-host")
  private implicit val apiGrpcPort = config.getInt("grpc.api-port")
  private implicit val channel = ManagedChannelBuilder.forAddress(apiGrpcHost, apiGrpcPort).usePlaintext(true).build

  def restore() = {
    log.debug("Start to restore actor system...")
    this.restoreBot()

    printActors(system)
    //this.restoreVisitor()
    //this.restoreConversation()
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
    val visitors = reply.data.parseJson.convertTo[List[Conversation]]
    visitors.map(visitor => {
      var botActor = Await.result(system.actorSelection("/user/" + visitor.bot).resolveOne(), shortTimeout.duration)
      botActor ! CreateConversation(visitor.id,visitor.bot,visitor.client)
    })
  }
}
