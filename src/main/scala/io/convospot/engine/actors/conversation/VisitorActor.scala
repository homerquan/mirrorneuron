package io.convospot.engine.actors.conversation
import akka.actor._
import io.convospot.engine.grpc.data.{JoinConversation, Say}

class VisitorActor(bot:ActorContext) extends Actor with ActorLogging {
  def receive = {
    case msg: JoinConversation=>
      bot.child(msg.conversation).get ! ConversationActor.Command.Subscribe()
    case msg: Say =>
      bot.child(msg.conversation).get ! ConversationActor.Command.Hear(self,msg.message)
    case msg: VisitorActor.Command.Hear =>
      log.info(msg.toString) //output to mq
    case msg: VisitorActor.Message.Response =>
      log.info(msg.toString)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

object VisitorActor {
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