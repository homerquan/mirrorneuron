package io.convospot.engine.actors.context

import akka.actor.{Actor, ActorContext, ActorLogging, Props}
import io.convospot.engine.actors.conversation.{HelperActor, VisitorActor}
import io.convospot.engine.util.ActorTrait
import io.convospot.engine.util.RedisConnector
import spray.json._
import io.convospot.engine.actors.context.output.JsonProtocol._
import io.convospot.engine.actors.context.output._


/**
  * Call GRPC or MQ to output
  */
private[convospot] class BotOutputActor(bot:ActorContext) extends Actor with ActorTrait with ActorLogging {
  val redis=RedisConnector.getRedis
  def receive = {
    case output: BotOutputActor.Message.OutputVisitorHear =>
      val message = CreateMessage(output.hear.from.path.name,output.hear.source,output.hear.message)
      redis.publish(output.channel, message.toJson.compactPrint)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

private[convospot] object BotOutputActor {
  def props(bot: ActorContext) = Props(new BotOutputActor(bot))
  sealed trait Message
  object Message {
    final case class OutputVisitorHear(channel: String, hear: VisitorActor.Command.Hear) extends Message
    final case class OutputHelperHear(channel: String, hear: HelperActor.Command.Hear) extends Message
  }
}

