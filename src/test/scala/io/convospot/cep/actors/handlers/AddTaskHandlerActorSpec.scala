package io.convospot.cep.actors.handlers


import akka.actor.{Actor, ActorContext, Props}
import akka.testkit.TestProbe
import io.convospot.cep.actors.MasterActor
import io.convospot.cep.config.RedisKeys
import io.convospot.cep.events.{Vehicle, GenericResponseMessage, GenericRequestMessage}
import io.convospot.engine.OptContainer
import io.convospot.engine.eta.GenericEta
import io.convospot.engine.selector.GenericSelector
import io.convospot.engine.vrp.GenericVRP
import io.convospot.util._
import org.specs2.Specification
import org.specs2.specification.BeforeEach
import scala.concurrent.duration._
import spray.json._
import io.convospot.cep.events.JsonProtocol._
import io.convospot.cep.events._

/**
 * Add task handler spec test
 * Because handler output into MQ, using a test version to redirect output
 * Created by homer on 6/26/15.
 */

class AddTaskHandlerTestActor(parentContext:ActorContext, optimizer:OptContainer)
  extends AddTaskHandlerActor(parentContext:ActorContext, optimizer:OptContainer)
  with HandlerActorRedirect

class AddTaskHandlerActorSpec extends Specification with BeforeEach {

  def before = {
  }

  def is = s2"""
  AddTaskHandlerActor should
    return id and emtpy eta after adding a task $addOne
  """

  def addOne = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = new CustomProbe(system)
        val testCase= TestJsonLoader.load("wise.command.task.add.json").parseJson
        val payload=testCase.convertTo[Task]
        val message = GenericRequestMessage("wise.message.request.task.add", payload, Map.empty[String,String])
        val optimizer=new OptContainer with GenericEta with GenericVRP with GenericSelector

        // Using a fabricated parent for setup context and mockup dependent actors
        val parent = system.actorOf(Props(new Actor{
          // inject dependency
          context.actorOf(Props(new Actor {
            def receive = {
              case _ => null
            }
          }),"fleet")
          val testActor = context.actorOf(Props(new AddTaskHandlerTestActor(context,optimizer)))
          def receive = {
            case evt:GenericResponseMessage if sender == testActor => probe.ref forward evt
            case evt:GenericRequestMessage => testActor forward evt
          }
        }),"test")

        parent.tell(message, probe.ref)

        // response id is not empty
        probe.expectMsgPF() {
          case GenericResponseMessage(_,payload:String,_,_)
            if (payload.parseJson.convertTo[IdAndBody[List[Map[String, Long]]]].id.length>0) &&
               (payload.parseJson.convertTo[IdAndBody[List[Map[String, Long]]]].body.size==0)
               => ()
        }
      }
    }
  }

}