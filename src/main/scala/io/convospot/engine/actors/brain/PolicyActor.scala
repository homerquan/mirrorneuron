package io.convospot.engine.actors.brain

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await
import io.convospot.engine.actors.brain.PolicyActor.Command
import io.convospot.engine.actors.conversation.HelperActor
import io.convospot.engine.constants.GrpcOutputCode.SUGGEST_RESPONSE
import io.convospot.engine.constants.Timeouts
import spray.json._
import io.convospot.engine.grpc.data.JsonProtocol._
import io.convospot.engine.grpc.data._
import io.convospot.engine.grpc.output.{Request, Response}
import io.convospot.engine.util.GrpcConsoleApiConnector


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
       val text = result.message.stripPrefix(",").stripSuffix(",").trim
       // demo knowledge only, Wait for semi auto mode (TODO: temp solution, MVP semi auto only)
       // TODO: add timeout handler
      val request = Request(typeCode = SUGGEST_RESPONSE, data = KnowledgeSuggestion("s",text,5).toJson.toString())
      val reply: Response = GrpcConsoleApiConnector.blockingStub.ask(request)

       // end wait
       sender ! HelperActor.Command.AnswerFromMachine(text)

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