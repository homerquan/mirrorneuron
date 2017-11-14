package io.convospot.engine.actors.conversation

import akka.actor._
import akka.cluster.sharding.ShardRegion

import scala.collection.immutable
import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.util.Timeout
import io.convospot.engine.actors.brain.PolicyActor
import io.convospot.engine.actors.common.Messages
import io.convospot.engine.actors.conversation.ConversationActor.{Command, Data, State}

import scala.concurrent.Await

private[convospot] class ConversationActor extends FSM[ConversationActor.State, ConversationActor.Data] with ActorLogging{
  val Task:String = ""
  val Context:String = ""
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

    /**
      * Setup Room id.
      * Subscribe Visitor.
      */
    case Event(msg@Command.Subscribe(_, _, _), _) =>
      sender ! UserActor.Message.Output(s"ROOM[${msg.id}]> Welcome, ${msg.name}!")
      goto(State.Active) using Data.Active(msg.id, immutable.HashMap(sender -> msg.name))

  }

  /**
    * State "Active" handler
    *
    * @return
    */
  when(State.Active) {
    /**
      * Subscribe new Visitor and notify subscribed Visitors.
      */
    case Event(msg@Command.Subscribe(_, _, _), stateData: Data.Active) =>
      for ((visitor: ActorRef, name: String) <- stateData.visitors) {
        visitor ! UserActor.Message.Output(s"ROOM[${stateData.id}] ${msg.name} has joined the room.")
      }
      sender ! UserActor.Message.Output(s"ROOM[${stateData.id}] Welcome, ${msg.name}!")
      stay using stateData.copy(
        visitors = stateData.visitors + (sender -> msg.name)
      )

    /**
      * Broadcast received message.
      */
    case Event(msg@Command.Message(_, message, sourceRole), stateData: Data.Active) =>
      stateData.visitors.get(sender) match {
        case Some(senderName) =>
          for ((visitor, name) <- stateData.visitors if visitor != sender) {
            //TODO: Demo AI here

          }
        case None =>
      }
      stay

    /**
      * Unsubscribe Visitor and notify subscribed Visitors.
      */
    case Event(msg@Command.Leave(_), stateData: Data.Active) =>
      stateData.visitors.get(sender) match {
        case Some(senderName) =>
          for ((visitor, name) <- stateData.visitors if visitor != sender) {
            visitor ! UserActor.Message.Output(s"ROOM[${stateData.id}] Visitor $senderName left room.")
          }
        case None =>
      }
      stay using stateData.copy(
        visitors = stateData.visitors - sender
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

  def props() = Props(new ConversationActor())
  val numberOfShards = 5
  val shardName: String = "io.convospot.engine.conversation"
  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.id, cmd)
  }
  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: Command => (math.abs(cmd.id.hashCode) % numberOfShards).toString
  }
  type Visitors = immutable.HashMap[ActorRef, String]
  sealed trait Command {
    def id: String
  }

  object Message {

  }

  object Command {

    /**
      * Subscribe
      *
      * @param id   Room id
      * @param name Visitor name
      */
    final case class Subscribe(id: String, name: String, role: String) extends Command

    /**
      * Leave Room
      *
      * @param id Room id
      */
    final case class Leave(id: String) extends Command

    /**
      * Chat Message
      *
      * @param id      Room id
      * @param message Chat message
      */
    final case class Message(id: String, message: String, sourceRole: String) extends Command

  }

  /**
    * FSM Data
    */
  sealed trait Data

  object Data {

    /**
      * Initial State
      */
    case object Initial extends Data

    /**
      * Active State
      *
      * @param id       Room Id
      * @param visitors Subscribed visitors
      */
    final case class Active(
                             id: String,
                             visitors: Visitors
                           ) extends Data

  }

  /**
    * FSM States
    */
  sealed trait State

  object State {
    case object Initial extends State
    case object Active extends State
    // wait human input
    // fully auto ...
  }

}