package io.convospot.engine.actors.context

import akka.actor.SupervisorStrategy.{Restart, Resume, Escalate}
import akka.actor._
import io.convospot.engine.actors.conversation.{ConversationActor,VisitorActor,HelperActor}
import io.convospot.engine.grpc.data.{CreateConversation,CreateVisitor,CreateHelper}
import io.convospot.engine.util.ActorTrait
import io.convospot.engine.constants.Timeouts

/**
  * The master actor for each bot
  * set context for each bot (multi-tenancy)
  * CQRS data flow into output and exception child actors
  */

private[convospot] class BotActor extends Actor with ActorTrait with ActorLogging {

  val outputActor=context.actorOf(Props(new BotOutputActor(context)),"outputActor")
  val exceptionActor=context.actorOf(Props(new BotExceptionActor(context)),"exceptionActor")

  def receive = {
    case msg: CreateConversation => {
      context.actorOf(Props(new ConversationActor(context)),msg.id)
    }
    case msg: CreateVisitor => {
      context.actorOf(Props(new VisitorActor(context)),msg.id)
    }
    case msg: CreateHelper => {
      context.actorOf(Props(new HelperActor(context)),msg.id)
    }
    case _ =>log.error("unsupported message in " + this.getClass.getSimpleName)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = Timeouts.MEDIAN) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: Exception => Escalate
    }

  override def preStart() {
    log.debug("A bot actor has created or recovered:" + self.path)
  }
}