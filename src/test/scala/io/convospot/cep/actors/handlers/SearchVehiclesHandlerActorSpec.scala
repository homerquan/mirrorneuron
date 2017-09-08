package io.convospot.cep.actors.handlers

import akka.actor.{Actor, Props, ActorContext}
import akka.testkit.TestProbe
import io.convospot.cep.events.{Vehicle, GenericResponseMessage, GenericRequestMessage}
import io.convospot.engine.OptContainer
import io.convospot.engine.eta.GenericEta
import io.convospot.engine.selector.GenericSelector
import io.convospot.engine.vrp.GenericVRP
import io.convospot.util.{TestJsonLoader, CustomProbe, HandlerActorRedirect, ActorsTestSupport}
import org.specs2.Specification
import scala.concurrent.duration._
import spray.json._
import io.convospot.cep.events.JsonProtocol._
import io.convospot.cep.events._

/**
 * Search vehicle handler spec test
 * Because handler output into MQ, using a test version to redirect output
 * Created by homer on 6/26/15.
 */

class SearchVehiclesHandlerTestActor(parentContext:ActorContext, optimizer:OptContainer)
  extends SearchVehiclesHandlerActor(parentContext:ActorContext, optimizer:OptContainer)
  with HandlerActorRedirect

class SearchVehiclesHandlerActorSpec extends Specification {

  def is = s2"""
  SearchVehiclesHandlerActor should
    return empty list initially $empty
  """

  def empty = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = new CustomProbe(system)
        val message = GenericRequestMessage("wise.message.request.vehicle.search", null, Map.empty[String,String])
        val optimizer=new OptContainer with GenericEta with GenericVRP with GenericSelector

        // Using a fabricated parent for setup context and mockup dependent actors
        val parent = system.actorOf(Props(new Actor{
          // inject dependency
          context.actorOf(Props(new Actor {
            def receive = {
              case evt: GenericRequestMessage => {
                evt.topic match {
                  case "wise.internal.request.vehicle.search" => {
                      sender ! GenericResponseMessage("wise.internal.response.vehicle.search", Map.empty[String, Vehicle], Map.empty[String, String])
                    }
                  }
              }
            }
          }),"fleet")
          val testActor = context.actorOf(Props(new SearchVehiclesHandlerTestActor(context,optimizer)),"searchVehiclesHandler")
          def receive = {
            case evt:GenericResponseMessage if sender == testActor => probe.ref forward evt
            case evt:GenericRequestMessage => testActor forward evt
          }
        }),"test")


        parent.tell(message, probe.ref)
        probe.expectMsgPF() {
          case GenericResponseMessage(_,payload:String,_,_)
            if (payload.parseJson.convertTo[Map[String,Vehicle]].size==0) => ()
        }
      }
    }
  }

}


