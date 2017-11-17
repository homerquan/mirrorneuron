package io.convospot.engine.actors.conversation

import akka.actor._
import scala.collection.immutable
import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.util.Timeout
import io.convospot.engine.actors.brain.PolicyActor
import io.convospot.engine.actors.common.Messages
import io.convospot.engine.actors.conversation.ConversationActor.{Command, Data, State}

import scala.concurrent.Await

private[convospot] class ConversationActor(bot:ActorRef) extends FSM[ConversationActor.State, ConversationActor.Data] with ActorLogging {

  /**
    * Initial state and data
    */
  startWith(State.Initial, Data.Initial)

  /**
    * State "Initial" handler
    *
    * @return
    */
  when(State.Initial) {

    case Event(msg@Command.Subscribe(), _) =>
      sender ! VisitorActor.Message.Response(s"Join conversation ${this.getClass.getSimpleName}")
      context.system.eventStream.subscribe(sender, classOf[Command.Say])
      goto(State.Active) using Data.Active(Some(sender),None)

    case Event(msg@Command.Supervise(), _) =>
      sender ! HelperActor.Message.Response(s"Join conversation ${this.getClass.getSimpleName}")
      context.system.eventStream.subscribe(sender, classOf[Command.Say])
      goto(State.Active) using Data.Active(None,Some(sender))

  }

  /**
    * State "Active" handler
    *
    * @return
    */
  when(State.Active) {
    /**
      * Can't Subscribe new Visitor.
      */
    case Event(msg@Command.Subscribe(), stateData: Data.Active) =>
      sender ! VisitorActor.Message.Response(s"Already taken by ${stateData.visitor.getClass.getSimpleName}")
      stay

    case Event(msg@Command.Hear, stateData: Data.Active) =>
      context.system.eventStream.publish(msg)
      stay

    /**
      * Unsubscribe visitor.
      */
    case Event(msg@Command.Leave(), stateData: Data.Active) =>
      context.system.eventStream.unsubscribe(sender, classOf[Command.Say])
      stay using stateData.copy(
        visitor = None
      )

    /**
      * Unsupervise helper.
      */
    case Event(msg@Command.Unsupervise(), stateData: Data.Active) =>
      context.system.eventStream.unsubscribe(sender, classOf[Command.Say])
      stay using stateData.copy(
        helper = None
      )

  }

  /**
    * Default handler
    */
  whenUnhandled {
    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  initialize()
}

private[convospot] object ConversationActor {

  def props(bot: ActorRef) = Props(new ConversationActor(bot))

  sealed trait Command

  object Command {

    final case class Subscribe() extends Command

    final case class Leave() extends Command

    final case class Supervise() extends Command

    final case class Unsupervise() extends Command

    final case class Hear(from: ActorRef, message: String) extends Command

  }


  sealed trait Data

  object Data {

    case object Initial extends Data

    final case class Active(
                             visitor: Option[ActorRef],
                             helper: Option[ActorRef]
                           ) extends Data


  }


  sealed trait State

  object State {

    case object Initial extends State

    case object Active extends State



  }

}