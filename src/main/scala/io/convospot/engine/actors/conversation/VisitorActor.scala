package io.convospot.engine.actors.conversation
import akka.actor._
import io.convospot.engine.grpc.data.{JoinConversation,Say}

class VisitorActor(bot:ActorRef) extends Actor with ActorLogging {
  def receive = {
    case msg: JoinConversation=>bot!ConversationActor.Command.Subscribe()
    case msg: Say =>
      //TODO: send with visitor's context
      bot!msg
    case msg: ConversationActor.Command.Hear =>
      log.info(msg.toString)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

object VisitorActor {
  def props(bot: ActorRef) = Props(new VisitorActor(bot))
  object Message {
    final case class Response(message: String)
  }
}