package io.convospot.engine.actors.conversation
import akka.actor._
import io.convospot.engine.actors.context.BotOutputActor
import io.convospot.engine.grpc.data.{JoinConversation, LeaveConversation, Say}

private[convospot] class VisitorActor(bot:ActorContext) extends Actor with ActorLogging {
  def receive = {
    case msg: JoinConversation=>
      bot.child(msg.conversation).get ! ConversationActor.Command.Subscribe()
    case msg: LeaveConversation=>
      bot.child(msg.conversation).get ! ConversationActor.Command.Leave()
    case msg: Say =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Hear(self,msg.message)
    case msg: VisitorActor.Command.Hear =>
      bot.child("outputActor").get ! BotOutputActor.Message.Output("d03a578e-ca1a-11e7-abc4-cec278b6b50a",msg.message)
    case msg: VisitorActor.Message.Response =>
      log.info(msg.toString)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

private[convospot] object VisitorActor {
  def props(bot: ActorContext) = Props(new VisitorActor(bot))
  sealed trait Message
  object Message {
    final case class Response(message: String) extends Message
  }
  sealed trait Command
  object Command {
    final case class Hear(from: ActorRef, message: String) extends Command
  }
}