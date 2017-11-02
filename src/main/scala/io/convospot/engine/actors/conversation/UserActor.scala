package io.convospot.engine.actors.conversation

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.ClusterSharding
import akka.io.Tcp
import akka.util.ByteString
import io.convospot.engine.actors.common.Messages

/**
  * Visitor. One for each connection.
  * Demonstrates context.become FSM solution
  * context is representing the observed behavior
  */
class UserActor(connection: ActorRef, role:String = "VISITOR") extends Actor with ActorLogging {

  import UserActor._

  val roomRegion = ClusterSharding(context.system).shardRegion(RoomActor.shardName)

  /**
    * Visitor name
    */
  var name: String = ""

  /**
    * Visitor room
    */
  var room: String = ""

  /**
    * Initial state
    */
  def receive = {
    case Message.Greeting =>
      connection ! encode("Welcome as "+role+"! Enter your name:")
    case Tcp.Received(data) =>
      val message = decode(data)
      name = message
      connection ! encode(s"$name, enter room name:")
      context.become(chooseRoomState)
  }

  /**
    * Visitor chooses room
    */
  def chooseRoomState: Receive = {
    case Tcp.Received(data) =>
      val message = decode(data)
      room = message
      roomRegion ! RoomActor.Command.Subscribe(room, name, role)
      sender ! nr
      context.become(inRoomState)
  }

  /**
    * Visitor in the room
    */
  def inRoomState: Receive = {
    case Tcp.Received(data) =>
      val message = decode(data)
      roomRegion ! RoomActor.Command.Message(room, message, role)
      sender ! nr
    case UserActor.Message.Output(message) =>
      connection ! encode(message)
    case Messages.Utterance(text:String,from:String, to:String) =>
      connection ! encode(s"[${from}]"+text)
    case Messages.Acknowledge(text:String) =>
      connection ! encode(s"[${text}]")
    case Tcp.PeerClosed =>
      roomRegion ! RoomActor.Command.Leave(room)
      context stop self
    case x => log.info(s"Visitor Unhandled: $x")
  }

}

object UserActor {

  def props(connection: ActorRef, role: String) = Props(new UserActor(connection, role))

  /**
    * Decode incoming data
    *
    * @param bytes Incoming bytes
    * @return
    */
  def decode(bytes: ByteString): String = bytes.decodeString("US-ASCII").trim

  /**
    * Encode outgoing data
    *
    * @param message Outgoing string
    * @return
    */
  def encode(message: String): Tcp.Write = Tcp.Write(ByteString(s"$message\n > "))

  /**
    * Empty invatation line
    *
    * @return
    */
  def nr: Tcp.Write = Tcp.Write(ByteString(s" > "))

  /**
    * Messages
    */
  object Message {

    /**
      * Greeting message request
      */
    case object Greeting

    /**
      * Outgoing message
      *
      * @param message
      */
    final case class Output(message: String)

  }

}