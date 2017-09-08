package io.convospot.engine.scheduler

import io.convospot.cep.events.ImmutableVehicleState
//import io.wisesystems.cep.events.JsonProtocol._
import io.convospot.cep.events.JsonProtocol._
import io.convospot.engine.scheduler.GenericPrecheduler
import io.convospot.util.TestJsonLoader
import org.specs2.Specification
import spray.json._

/**
 * Created by homer on 8/23/15.
 */
class GenericRtaSpec extends Specification {

  def is = sequential ^ s2"""
        Delay Unit testing
        1. No delay    assignment1
        2. Two delays    assignment2
        3. One at risk less than 30 min    assignment3
        4. One at risk less than 10 min    assignment4
        5. If the first task is at risk dont trigger RTA assignment9
        6- update loc error 10/21  $assignment12

        Real Time Adjustment Unit Testing
        1. Empty Schedule noAssignment
        2. Two delayed assignments assignment5
        3. Switching assignments assignment6
        4. Vehicle based on the order assignment7
        5. Vehicle based on the order assignment8
        6. Vehicle based on the order assignment10
        7. Vehicle based on the order assignment11

        With custom order
        1. specify the first assignment lockedAssog1
        2. specify the first and second assignments lockedAssog2
        3. issue with one assignment locked lockedAssign3
        4. issue with one assignment locked lockedAssign4
    """


  def assignment1 = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_2_assignments.json").parseJson
//    val testVehicleState= TestJsonLoader.load("vehicleNoAssig_10.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1442232000)
    results._1.code should beEqualTo(100)
  }

  def assignment2 = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_2_assignments.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1442264400)
    results._1.code should beEqualTo(100)
  }

  def assignment3 = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_2_assignments.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1442261000)
    results._1.code should beEqualTo(201)
  }

  def assignment4 = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_2_assignments.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1442263100)
    results._1.code should beEqualTo(201) or beEqualTo(202)
  }

  def noAssignment = {
    val testVehicleState = TestJsonLoader.load("vehicle_state_no_assignment.json").parseJson
    val vs = testVehicleState.convertTo[ImmutableVehicleState]
    //    val jsn=vs.toJson
    val results = GenericPrecheduler.realtimeSchedule(vs)
    results must empty
  }

  def assignment5 = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_2_assignments_RTA.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1442264400)
    results(0).assignment should contain ("efa8eca0-5d45-11e5-b39d-bf7323c05163")
  }

  def assignment6 = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_2_assignments_RTA2.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1442264400)
    results(0).assignment should contain ("eef41b90-5d45-11e5-b39d-bf7323c05163")
  }

  def assignment7 = {
    val testVehicleState= TestJsonLoader.load("vehicleNoAssig_10Lock.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1443020000) //1443020000  1442232000
    results(0).assignment should contain ("9b435440-6243-11e5-8b9e-6364b9edf94d")
  }
  def assignment8 = {
    val testVehicleState= TestJsonLoader.load("VehState_1_11111.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1443200000) //1443020000  1442232000
    results(0).assignment should contain ("0b9bf490-63a2-11e5-a5c7-0f2d44cdb8b4")
  }

  def lockedAssog1 = {
    val testVehicleState= TestJsonLoader.load("vehicleNoAssig_10_Locked1.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1443020000) //1443020000  1442232000
    results(0).assignment should contain ("9c2ace10-6243-11e5-8b9e-6364b9edf94d")
  }

  def lockedAssog2 = {
    val testVehicleState= TestJsonLoader.load("vehicleNoAssig_10_Locked2.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1443020000) //1443020000  1442232000
    results(0).assignment should contain ("9c2ace10-6243-11e5-8b9e-6364b9edf94d")
  }

  def lockedAssign3 = {
    val testVehicleState= TestJsonLoader.load("VehStateOneAssign.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1444919400) //1443020000  1442232000
    val results2=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1444919400)
    results(0).assignment should contain ("12695d10")
  }


  def lockedAssign4 = {
    val testVehicleState= TestJsonLoader.load("VehStateOneAssign_2.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
//    val results=GenericPrecheduler.realtimeSchedule(vs,1444923424) //1443020000  1442232000
    val results2=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1444923424)
    (0) should beEqualTo(0)
  }

  def assignment9 = {
    val testVehicleState= TestJsonLoader.load("VehState_firstTaskatRisk.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1444779000)
    results._1.code should beEqualTo(201)
  }

  def assignment10 = {
    val testVehicleState= TestJsonLoader.load("VehState_route04-10132015_Debug.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeSchedule(vs,1444753800) //1444753800
    (0) should beEqualTo(0)
  }


  def assignment11 = {

    val testVehicleState= TestJsonLoader.load("VehState_Seq1.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1445000400)
    val results2=GenericPrecheduler.realtimeSchedule(vs,1445000400) //1444753800
    (0) should beEqualTo(0)

  }


  def assignment12 = {
    val testVehicleState= TestJsonLoader.load("ERROR_UpdateLoc_1").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val results=GenericPrecheduler.realtimeWarningAndUpdateDelivery(vs,1445000400)
//    val results2=GenericPrecheduler.realtimeSchedule(vs,1445000400) //1444753800
    (0) should beEqualTo(0)

  }


}
