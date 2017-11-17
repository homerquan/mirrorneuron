package io.convospot.engine.actors.conversation
import akka.actor._
import io.convospot.engine.grpc.data.Say

class HelperActor (bot: ActorContext) extends Actor with ActorLogging {
  def receive = {
    case msg: Say =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Hear(self,msg.message)
    case msg: HelperActor.Command.Hear =>
      log.info(msg.toString)
    case msg: HelperActor.Message.Response =>
      log.info(msg.toString)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

object HelperActor {
  def props(bot: ActorContext) = Props(new HelperActor(bot))
  sealed trait Message
  object Message {
    final case class Response(message: String) extends Message
  }
  sealed trait Command
  object Command {
    final case class Hear(from: ActorRef, message: String) extends Command
  }
}