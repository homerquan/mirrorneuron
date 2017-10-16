package io.convospot.engine.actors.context

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor._

import scala.concurrent.duration._


/**
  * The master actor
  * 1. set context for each client (multi-tenancy)
  * 2. routing events
  */

private[convospot] class MasterActor() extends Actor with ActorLogging {

  def receive = {
    case _ => log.error("unsupported message in MasterActor")
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 30 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: Exception => Restart
    }

  override def preStart() {
    log.debug("A master actor has created or recovered:" + self.path)
  }
}