package io.convospot.engine.actors.context

import akka.actor.SupervisorStrategy.{Restart, Resume, Escalate}
import akka.actor._
import io.convospot.engine.actors.conversation.{ConversationActor,VisitorActor}
import io.convospot.engine.grpc.data.{CreateConversation,CreateVisitor}
import io.convospot.engine.util.ActorTrait

import scala.concurrent.duration._


/**
  * The master actor for each bot
  * set context for each bot (multi-tenancy)
  * CQRS data flow into output and exception child actors
  */

private[convospot] class BotActor extends Actor with ActorTrait with ActorLogging {

  val outputActor=context.actorOf(Props(new BotOutputActor(self.path.name)),"outputActor")
  val exceptionActor=context.actorOf(Props(new BotExceptionActor(self.path.name)),"exceptionActor")

  def receive = {
    case msg: CreateConversation => {
      context.actorOf(Props(new ConversationActor(self)),msg.id)
    }
    case msg: CreateVisitor => {
      context.actorOf(Props(new VisitorActor(self)),msg.id)
    }
    case _ =>log.error("unsupported message in " + this.getClass.getSimpleName)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 30 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: Exception => Escalate
    }

  override def preStart() {
    log.debug("A master actor has created or recovered:" + self.path)
  }
}