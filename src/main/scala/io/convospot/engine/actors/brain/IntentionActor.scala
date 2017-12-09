package io.convospot.engine.actors.brain

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor._
import io.convospot.engine.actors.brain.IntentionActor.{Data, State}
import io.convospot.engine.constants.Timeouts
import io.convospot.engine.grpc.data.Analytics

import scala.collection.SortedSet
import scala.collection.immutable.Queue

/**
  * Understand what's visitor's Intentions
  * it's a probabilistic graphic model to predict visitor's intention
  * It's a Sequence Classification Problem
  * Update the model using online learning
  * @param visitor
  */
private[convospot] class IntentionActor(visitor:ActorContext) extends FSM[IntentionActor.State, IntentionActor.Data] with ActorLogging {


  startWith(State.Active, Data.Active(Queue.empty[Analytics]))
  // only consider limited window of events
  val windowSize = 5

  when(State.Active) {
    case Event(msg: Analytics, stateData: Data.Active) =>
      //TODO: sequence labeling here
      if(stateData.window.length<=windowSize) {
        stay using stateData.copy(
          window = stateData.window.enqueue(msg)
        )
      } else {
        val (_, updatedQueue) = stateData.window.dequeue
        stay using stateData.copy(
          window = updatedQueue
        )
      }
  }

  /**
    * Default handler
    */
  whenUnhandled {
    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = Timeouts.MEDIAN) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: Exception => Escalate
    }

  override def preStart() {
    log.debug("A intention actor has created or recovered:" + self.path)
  }

  initialize()

}

private[convospot] object IntentionActor {
  def props(visitor: ActorContext) = Props(new IntentionActor(visitor))

  sealed trait Data

  object Data {
    final case class Active(window:Queue[Analytics]) extends Data
  }


  sealed trait State

  object State {
    case object Active extends State
  }

}