package io.convospot.engine.scheduler

import io.convospot.cep.events.{ImmutableVehicleState, _}

//import io.wisesystems.cep.events.JsonProtocol._
import io.convospot.cep.events.JsonProtocol._
import io.convospot.util.TestJsonLoader
import org.specs2.Specification
import spray.json._

/**
 * Created by homer on 8/23/15.
 */
class RTAosrmSpec extends Specification {

  def is = sequential ^ s2"""
        add task spec
        0. check the current route warningFirst
        1. RTA with OSRM    scheduleAssignment
        2. RTA with SORM 20 task $checkImpact
      |

    """

  def warningFirst = {
    // @homer, the json file name convention "for_example_json_file.json"
    val testVehicleState= TestJsonLoader.load("vehicle_state_trav_test.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    // @homer, the scala func name convention "abcWord"
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1443020000) //1443020000  1442232000
    results._2(0).assignment should contain ("6364b9edf94d")
  }

  def scheduleAssignment = {
    // @homer, the json file name convention "for_example_json_file.json"
    val testVehicleState= TestJsonLoader.load("vehicle_state_trav_test.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    // @homer, the scala func name convention "abcWord"
    val results=GenericPrecheduler.estimateRTA_osrm(vs,1443020000) //1443020000  1442232000
    results.delayAssignments(0) should contain ("6364b9edf94d")
  }

  def checkImpact = {
    // @homer, the json file name convention "for_example_json_file.json"
    val testVehicleState= TestJsonLoader.load("VehicleState_20Task.json").parseJson
//    val testVehicleState= TestJsonLoader.load("VehicleState_30Task.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    // @homer, the scala func name convention "abcWord"
    val results=GenericPrecheduler.estimateRTA_osrm(vs,1443716627)
//    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1443716627)
//        val results=GenericPrecheduler.realtimeSchedule(vs,1443716627)
        results.changeTime should be > -19999999L   // OSRM
//        results(0).delay should be > -1  //realtime schedule
//    results._1.code should be > -1 // warning
  }

}
