package io.convospot.engine.actors.conversation

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor._
import io.convospot.engine.actors.brain.{IntentionActor, PolicyActor}
import io.convospot.engine.actors.context.BotOutputActor
import io.convospot.engine.grpc.data._
import io.convospot.engine.actors.conversation.VisitorActor.{Command, Data, State}
import io.convospot.engine.constants.Timeouts

import scala.collection.SortedSet

/**
  * Map visitor into a digital actor
  * State data includes predicted intentions
  *
  * @param bot
  */

private[convospot] class VisitorActor(bot: ActorContext) extends FSM[VisitorActor.State, VisitorActor.Data] with ActorLogging {

  val intentionActor = context.actorOf(Props(new IntentionActor(context)), "intention_actor")

  startWith(State.Online, Data.Online(SortedSet.empty[String]))

  when(State.Offline) {
    case Event(msg: OnlineVisitor,_) =>
      goto(State.Online) using Data.Online(SortedSet.empty[String])
  }

  when(State.Online) {
    case Event(msg: JoinConversation, _) =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Subscribe()
      stay
    case Event(msg: LeaveConversation, _) =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Leave()
      stay
    case Event(msg: Say, _) =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Hear(self, "visitor", msg.message)
      stay
    case Event(msg: VisitorActor.Command.Hear, _) =>
      val conversation = sender.path.name
      bot.child("outputActor").get ! BotOutputActor.Command.OutputVisitorHear(self.path.name, msg, conversation)
      stay
    case Event(msg: VisitorActor.Message.Response, _) =>
      log.info(msg.toString)
      stay
    case Event(msg: OfflineVisitor,_) =>
      goto(State.Offline) using Data.Offline(SortedSet.empty[String])
    case Event(msg: Analytics, _) =>
      intentionActor ! msg
      stay
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
    log.debug("A visitor actor has created or recovered:" + self.path)
  }

  initialize()
}

private[convospot] object VisitorActor {
  def props(bot: ActorContext) = Props(new VisitorActor(bot))

  sealed trait Message

  object Message {

    final case class Response(message: String) extends Message

  }

  sealed trait Command

  object Command {

    final case class Hear(from: ActorRef, source: String, message: String) extends Command

  }

  sealed trait Data

  object Data {

    final case class Online(intention:SortedSet[String]) extends Data

    final case class Offline(intention:SortedSet[String]) extends Data

  }


  sealed trait State

  object State {

    case object Online extends State

    case object Offline extends State

  }

}