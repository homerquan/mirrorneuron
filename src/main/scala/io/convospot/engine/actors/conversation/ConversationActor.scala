package io.convospot.engine.actors.conversation

import akka.actor._

import scala.collection.immutable
import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.util.Timeout
import io.convospot.engine.actors.brain.PolicyActor
import io.convospot.engine.actors.common.Messages
import io.convospot.engine.actors.conversation.ConversationActor.Mode._
import io.convospot.engine.actors.conversation.ConversationActor.{Command, Data, State}
import io.convospot.engine.grpc.data.SwitchConversationMode

import scala.concurrent.Await

private[convospot] class ConversationActor(bot: ActorContext) extends FSM[ConversationActor.State, ConversationActor.Data] with ActorLogging {


  val observer = Some(context.actorOf(Props(new PolicyActor(context)), "policy_actor"))

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

    case Event(msg: Command.Subscribe, _) =>
      sender ! VisitorActor.Message.Response(s"Join conversation ${this.getClass.getSimpleName}")
      goto(State.Active) using Data.Active(Some(sender), None, Semi)

  }

  /**
    * State "Active" handler
    *
    * @return
    */
  when(State.Active) {

    case Event(msg: Command.Subscribe, stateData: Data.Active) =>
      sender ! VisitorActor.Message.Response(s"Re-join conversation ${this.getClass.getSimpleName}")
      stay using stateData.copy(
        visitor = Some(sender)
      )

    case Event(msg: Command.Leave, stateData: Data.Active) =>
      sender ! VisitorActor.Message.Response(s"Leave conversation ${this.getClass.getSimpleName}")
      stay using stateData.copy(
        visitor = None
      )

    case Event(msg: Command.Supervise, stateData: Data.Active) =>
      sender ! HelperActor.Message.Response(s"Supervise conversation ${this.getClass.getSimpleName}")
      stay using stateData.copy(
        helper = Some(sender)
      )
    case Event(msg: Command.Unsupervise, stateData: Data.Active) =>
      sender ! HelperActor.Message.Response(s"Stop supervise conversation ${this.getClass.getSimpleName}")
      stay using stateData.copy(
        helper = None
      )

    case Event(msg: Command.Hear, stateData: Data.Active) =>

      /**
        * Iu auto mode, AI answer directly
        * In semi mode, hear and ask AI, send best guess answer before timeout
        * In Manual mode, forward to the other party
        */
      stateData.mode match {
        case Semi => {
          if (stateData.visitor!= None && msg.from == stateData.visitor.get && stateData.helper != None)
            stateData.helper.get ! HelperActor.Command.Hear.tupled(Command.Hear.unapply(msg).get)
          if (stateData.helper!= None && msg.from == stateData.helper.get && stateData.visitor != None)
            stateData.visitor.get ! VisitorActor.Command.Hear.tupled(Command.Hear.unapply(msg).get)
        }
//        case Auto => {
//          if (sta
        // teData.visitor!= None && msg.from == stateData.visitor.get)
//            stateData.visitor.get ! VisitorActor.Command.Hear(observer,"babababa from A.I.")
//        }
        case Manual => {
          if (stateData.visitor!= None && msg.from == stateData.visitor.get && stateData.helper != None)
            stateData.helper.get ! HelperActor.Command.Hear.tupled(Command.Hear.unapply(msg).get)
          if (stateData.helper!= None && msg.from == stateData.helper.get && stateData.visitor != None)
            stateData.visitor.get ! VisitorActor.Command.Hear.tupled(Command.Hear.unapply(msg).get)
        }
      }
      stay

    /**
      * Unsubscribe visitor.
      */
    case Event(msg: Command.Leave, stateData: Data.Active) =>
      stay using stateData.copy(
        visitor = None
      )

    case Event(msg: Command.Unsupervise, stateData: Data.Active) =>
      stay using stateData.copy(
        helper = None
      )

    case Event(msg: SwitchConversationMode, stateData: Data.Active) =>
      var mode = stateData.mode
      if (msg.mode == "semi") mode=Semi
      if (msg.mode == "auto") mode=Auto
      if (msg.mode == "manual") mode=Manual
      stay using stateData.copy(
        mode = mode
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

  def props(bot: ActorContext) = Props(new ConversationActor(bot))

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
                             helper: Option[ActorRef],
                             mode: Mode
                           ) extends Data


  }

  sealed trait Mode

  object Mode {

    case object Auto extends Mode

    case object Semi extends Mode

    case object Manual extends Mode

  }


  sealed trait State

  object State {

    case object Initial extends State

    case object Active extends State


  }

}