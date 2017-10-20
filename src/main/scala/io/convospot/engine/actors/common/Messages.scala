package io.convospot.engine.actors.common
import java.time.Instant
import akka.actor.ActorRef

private[convospot] object Messages {

  sealed trait Event{
    def text: String = ""
    def timestamp: Long = Instant.now.getEpochSecond
  }

  final case class Utterance(override val text: String, from: String, to: String) extends Event

  final case class Action(override val text: String) extends Event

  final case class Acknowledge(override val text: String) extends Event

  final case class OutputAI(toHelper:Event, toVisitor:Event)

  final case class Conversation(context: String, dialog: List[Event])

  final case class RequestMessage(topic: String, request: Any, options: Map[String, String] = Map.empty[String, String], sender: ActorRef = null)

  final case class ResponseMessage(topic: String, response: Any, options: Map[String, String] = Map.empty[String, String], sender: ActorRef = null)

}