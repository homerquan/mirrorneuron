package io.convospot.engine.actors.context

import akka.actor.{Actor, ActorContext, ActorLogging, Props}
import io.convospot.engine.util.ActorTrait
import io.convospot.engine.util.RedisConnector

/**
  * Call GRPC or MQ to output
  */
private[convospot] class BotOutputActor(bot:ActorContext) extends Actor with ActorTrait with ActorLogging {
  val redis=RedisConnector.getRedis
  def receive = {
    case output: BotOutputActor.Message.Output =>
      redis.publish(output.channel, output.message)
    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

private[convospot] object BotOutputActor {
  def props(bot: ActorContext) = Props(new BotOutputActor(bot))
  sealed trait Message
  object Message {
    final case class Output(channel: String, message: String) extends Message
  }
}