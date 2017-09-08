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
 * Add vehicle handler spec test
 * Because handler output into MQ, using a test version to redirect output
 * Created by homer on 6/26/15.
 */

class AddVehicleHandlerTestActor(parentContext:ActorContext, optimizer:OptContainer)
  extends AddVehicleHandlerActor(parentContext:ActorContext, optimizer:OptContainer)
  with HandlerActorRedirect

class AddVehicleHandlerActorSpec extends Specification with BeforeEach {

  def before = {
  }

  def is = s2"""
  AddVehiclesHandlerActor should
    return id after adding a vehicle $addOne
  """

  def addOne = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = new CustomProbe(system)
        val testCase= TestJsonLoader.load("wise.command.vehicle.add.json").parseJson
        val payload=testCase.convertTo[Vehicle]
        val message = GenericRequestMessage("wise.message.request.vehicle.add", payload, Map.empty[String,String])
        val optimizer=new OptContainer with GenericEta with GenericVRP with GenericSelector

        // Using a fabricated parent for setup context and mockup dependent actors
        val parent = system.actorOf(Props(new Actor{
          // inject dependency
          context.actorOf(Props(new Actor {
            def receive = {
              case _ => Nil
            }
          }),"fleet")
          val testActor = context.actorOf(Props(new AddVehicleHandlerTestActor(context,optimizer)))
          def receive = {
            case evt:GenericResponseMessage if sender == testActor => probe.ref forward evt
            case evt:GenericRequestMessage => testActor forward evt
          }
        }),"test")

        parent.tell(message, probe.ref)

        // response id is not empty
        probe.expectMsgPF() {
          case GenericResponseMessage(_,id:String,_,_) if id.length>0 => ()
        }
      }
    }
  }

}