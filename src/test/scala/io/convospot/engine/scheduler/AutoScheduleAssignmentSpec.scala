package io.convospot.engine.scheduler

import io.convospot.cep.events.ImmutableVehicleState
import io.convospot.cep.events.Task
import io.convospot.cep.events._
import io.convospot.engine.scheduler.{FunctionsVRPs, GenericPrecheduler}
import io.convospot.util.TestJsonLoader

//import io.wisesystems.cep.events.JsonProtocol._
import io.convospot.cep.events.JsonProtocol._
import io.convospot.util.TestJsonLoader
import org.specs2.Specification
import spray.json._

/**
 * Created by homer on 8/23/15.
 */
class AutoScheduleAssignmentSpec extends Specification {

  def is = sequential ^ s2"""
        add task spec
        1. two old assignments and one new assignment    scheduleAssignment
        2. no assignment    noAssigments
        3. no available schedule $noSchedule
        4. Homer error1 $error1
        5.  error2 $error2
      |

    """


  def scheduleAssignment = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_AddTasks.json").parseJson //keep this same
    val testOneTask= TestJsonLoader.load("schedule_one_assignment_keep_rest_unchanged.json").parseJson // assignment here
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val assign=testOneTask.convertTo[Assignment]
    //Current Time
    val results=GenericPrecheduler.scheduleOneAssignment(vs,assign,1442232000)
    results(2).assignment should contain ("3becdee0-6152-11e5-8966-8b9152115413")
  }

  def noSchedule = {
    val testVehicleState= TestJsonLoader.load("vehicle_state_no_Assign.json").parseJson //keep this same
    val testOneTask= TestJsonLoader.load("NewAssignment.json").parseJson // assignment here
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val assign=testOneTask.convertTo[Assignment]
    //Current Time
//    val jsn=state.getImmutableState.toJson
//    FunctionsVRPs.saveInFile(jsn.toString,"vehicleNoAssig")
//    val jsn2=assignment.toJson
//    FunctionsVRPs.saveInFile(jsn2.toString,"assign")

    val results=GenericPrecheduler.scheduleOneAssignment(vs,assign,1442232000)
    results(0).assignment should contain ("7e429020-6169-11e5-aa49-8d467ddc79c6")
  }

  def error1 = {
    val testVehicleState= TestJsonLoader.load("vehicle_immutable_state_error.json").parseJson //keep this same
    val testOneTask= TestJsonLoader.load("assignment_error.json").parseJson // assignment here
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val assign=testOneTask.convertTo[Assignment]
    //Current Time
    //    val jsn=state.getImmutableState.toJson
    //    FunctionsVRPs.saveInFile(jsn.toString,"vehicleNoAssig")
    //    val jsn2=assignment.toJson
    //    FunctionsVRPs.saveInFile(jsn2.toString,"assign")

    val results=GenericPrecheduler.scheduleOneAssignment(vs,assign,1442415695)
    results(0).assignment should contain ("a7368360-620e-11e5-b861-773e23f4b8e9")
  }

  def error2 = {
    val testVehicleState= TestJsonLoader.load("vehicleNoAssig_10.json").parseJson //keep this same
    val testOneTask= TestJsonLoader.load("assign_10.json").parseJson // assignment here
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val assign=testOneTask.convertTo[Assignment]

    val results=GenericPrecheduler.scheduleOneAssignment(vs,assign,1442984400)
    results(0).assignment should contain ("9c2ace10-6243-11e5-8b9e-6364b9edf94d")
  }

}
