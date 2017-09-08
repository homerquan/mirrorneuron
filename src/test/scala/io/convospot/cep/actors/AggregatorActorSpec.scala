//package io.wisesystems.cep.actors
//
//import akka.actor.{Actor, Props, ActorContext}
//import io.wisesystems.cep.config.RedisKeys
//import io.wisesystems.cep.events.{Vehicle, GenericResponseMessage, GenericRequestMessage}
//import io.wisesystems.util.{RedisConnector, TestJsonLoader, CustomProbe, ActorsTestSupport}
//import org.scalamock.specs2.{IsolatedMockFactory}
//import org.specs2.Specification
//import scala.concurrent.duration._
//import spray.json._
//import io.wisesystems.cep.events.JsonProtocol._
//import io.wisesystems.cep.events._
//
///**
// * AggregatorActor spec test
// * Created by homer on 6/26/15.
// */
//
//class AggregatorActorSpec extends Specification with IsolatedMockFactory {
//
//  def before = {
//    val redis=RedisConnector.getRedis
//    val vehicleNameKey=RedisKeys.VEHICLE_NAME_MAP_PREFIX + "akka://wise-cep-test/user/test"
//    redis.del(vehicleNameKey)
//  }
//
//  def is = s2"""
//  AggregatorActor should
//    return all vechile state $aggregate
//  """
//
//  def aggregate = {
//    new ActorsTestSupport {
//      within(1 second) {
//        val probe = new CustomProbe(system)
//        val message = GenericRequestMessage("wise.internal.request.aggregator.vehicle", List.empty[String], Map.empty[String,String])
//
//        // Using a fabricated parent for setup context and mockup dependent actors
//        val parent = system.actorOf(Props(new Actor{
//          val testActor = context.actorOf(Props(new AggregatorActor(self,Map.empty[String,String])))
//          def receive = {
//            case evt:GenericResponseMessage if sender == testActor => probe.ref forward evt
//            case evt:GenericRequestMessage => testActor forward evt
//          }
//        }),"test")
//
//        parent.tell(message, probe.ref)
//        probe.expectMsgType[GenericResponseMessage]
////        probe.expectMsgPF() {
////          case GenericResponseMessage(_,payload:String,_)
////            if payload.length==0 => ()
////        }
//      }
//    }
//  }
//
//}


