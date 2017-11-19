package io.convospot.engine.actors.conversation
import akka.actor._
import io.convospot.engine.actors.context.BotOutputActor
import io.convospot.engine.grpc.data.{Say, SuperviseConversation}

private[convospot] class HelperActor (bot:ActorContext) extends Actor with ActorLogging {
  def receive = {
    case msg: SuperviseConversation=>
      bot.child(msg.conversation).get ! ConversationActor.Command.Supervise()
    case msg: Say =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Hear(self,msg.message)
    case msg: HelperActor.Command.Hear =>
      bot.child("outputActor").get ! BotOutputActor.Message.Output("68ad82ea-ca32-11e7-abc4-cec278b6b50a",msg.message)
    case msg: HelperActor.Message.Response =>
      log.info(msg.toString)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

private[convospot] object HelperActor {
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