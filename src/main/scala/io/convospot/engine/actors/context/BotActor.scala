package io.convospot.engine.actors.context

import akka.actor.SupervisorStrategy.{Restart, Resume, Escalate}
import akka.actor._
import io.convospot.engine.actors.conversation.ConversationActor
import io.convospot.engine.grpc.data.CreateConversation
import io.convospot.engine.util.ActorTrait

import scala.concurrent.duration._


/**
  * The master actor for each bot
  * set context for each bot (multi-tenancy)
  */

private[convospot] class BotActor() extends Actor with ActorTrait with ActorLogging {

  def receive = {
    case msg: CreateConversation => context.actorOf(Props(new ConversationActor()),msg.id)
    case _ => log.error("unsupported message in MasterActor")
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 30 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: Exception => Escalate //send to a error channel for bot_id
    }

  override def preStart() {
    log.debug("A master actor has created or recovered:" + self.path)
  }
}