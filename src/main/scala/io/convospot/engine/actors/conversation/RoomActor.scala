package io.convospot.engine.actors.conversation

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.cluster.sharding.ShardRegion
import scala.collection.immutable
import io.convospot.engine.actors.brain._
/**
  * Chat room
  * Demonstrates akka.actor.FSM solution
  */
class RoomActor extends FSM[RoomActor.State, RoomActor.Data] with ActorLogging {

  import RoomActor._

  // TODO: demo only
  val observer = context.actorOf(Props[PolicyActor],"sample_policy_actor")

  var conversation = ""  //TODO: demo a very simple context -- only last sentences

  /**
    * Initial state and data
    */
  startWith(State.Initial, Data.Initial)

  /**
    * State "Initial" handler
    * @return
    */
  when(State.Initial) {

    /**
      * Setup Room id.
      * Subscribe Visitor.
      */
    case Event(msg@Command.Subscribe(_, _, _), _) =>
      sender ! UserActor.Message.Direct(s"ROOM[${msg.id}]> Welcome, ${msg.name}!")
      goto(State.Active) using Data.Active(msg.id, immutable.HashMap(sender -> msg.name))

  }

  /**
    * State "Active" handler
    * @return
    */
  when(State.Active) {
    /**
      * Subscribe new Visitor and notify subscribed Visitors.
      */
    case Event(msg@Command.Subscribe(_, _, _), stateData: Data.Active) =>
      for ((visitor: ActorRef, name: String) <- stateData.visitors) {
        visitor ! UserActor.Message.Direct(s"ROOM[${stateData.id}] ${msg.name} has joined the room.")
      }
      sender ! UserActor.Message.Direct(s"ROOM[${stateData.id}] Welcome, ${msg.name}!")
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
            implicit val timeout = Timeout(15 seconds)
            val future = observer ? PolicyActor.Message.InFromRoom(message, sourceRole, conversation)
            val result = Await.result(future, timeout.duration).asInstanceOf[RoomActor.Message.FromAI]
            if (sourceRole == "HELPER") {
              sender ! UserActor.Message.Generic(s"${result.toHelper} ", "AI")
              visitor ! UserActor.Message.Generic(s"${result.toVisitor} ", "AI")
            } else {
              conversation = result.toHelper
              visitor ! UserActor.Message.Generic(s"${result.toHelper} ", "AI")
              sender ! UserActor.Message.Generic(s"${result.toVisitor} ", "AI")
            }
            // visitor ! VisitorActor.Message.Direct(s"[$senderName] $message")
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
            visitor ! UserActor.Message.Direct(s"ROOM[${stateData.id}] Visitor $senderName left room.")
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

object RoomActor {

  def props() = Props(new RoomActor())

  /**
    * Number of shards
    */
  val numberOfShards = 2

  /**
    * Shard Name
    */
  val shardName: String = "org.example.chat.Room"

  /**
    * Shard Id extractor
    */
  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.id, cmd)
  }

  /**
    * Shard resolver
    */
  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: Command => (math.abs(cmd.id.hashCode) % numberOfShards).toString
  }

  /**
    * Subscribed Visitors hashmap
    * Visitor ref -> Visitor name
    */
  type Visitors = immutable.HashMap[ActorRef, String]

  /**
    * Room commands
    */
  sealed trait Command {
    /**
      * Destination Room Id
      * @return
      */
    def id: String
  }

  object Message {

    final case class FromAI(toVisitor: String, toHelper: String)

  }

  object Command {

    /**
      * Subscribe
      *
      * @param id Room id
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
      * @param id Room id
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
      * @param id Room Id
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

    /**
      * Initial
      */
    case object Initial extends State

    /**
      * Active
      */
    case object Active extends State

  }

}