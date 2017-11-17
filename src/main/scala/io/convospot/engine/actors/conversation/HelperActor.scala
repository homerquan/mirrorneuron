package io.convospot.engine.actors.conversation
import akka.actor._
import io.convospot.engine.grpc.data.Say

class HelperActor (bot: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case msg: Say =>
      bot!msg
    case msg: ConversationActor.Command.Hear =>
      log.info(msg.toString)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

object HelperActor {
  def props(bot: ActorRef) = Props(new HelperActor(bot))
  object Message {
    final case class Response(message: String)
  }
}