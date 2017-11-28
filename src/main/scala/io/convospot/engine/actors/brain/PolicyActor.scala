package io.convospot.engine.actors.brain

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

import scala.concurrent.Await
import io.convospot.engine.actors.brain.PolicyActor.Command
import io.convospot.engine.actors.conversation.HelperActor
import io.convospot.engine.constants.Timeouts


case object AskNameMessage

private[convospot] class PolicyActor(bot:ActorContext) extends Actor with ActorLogging{

  val languageActor = context.actorOf(Props(new LanguageActor(bot)), "language_actor")
  val knowledgeActor = context.actorOf(Props(new KnowledgeActor(bot)), "knowledge_actor")
  implicit val timeout = Timeout(Timeouts.MEDIAN)

  def receive = {
    case msg: Command.Ask =>
       //TODO: only trying to understand language here, it should go KB and Action later
       //Using route here
       val future = knowledgeActor ? KnowledgeActor.Command.Ask(msg.message)
       val result = Await.result(future, Timeouts.MEDIAN).asInstanceOf[Command.AnswerFromKnowledge]
       sender ! HelperActor.Command.AnswerFromMachine(result.message)

    case _ => log.error("unsupported message in " + this.getClass.getSimpleName)
  }
}

object PolicyActor {

  sealed trait Command
  object Command {
    final case class Ask(message: String) extends Command
    final case class Learn(context: String) extends Command
    final case class AnswerFromKnowledge(message: String) extends Command
  }

}