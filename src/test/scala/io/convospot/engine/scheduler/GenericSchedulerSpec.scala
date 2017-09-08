package io.convospot.engine.scheduler

import io.convospot.cep.events._
//import io.wisesystems.cep.events.JsonProtocol._
import io.convospot.cep.events.JsonProtocol._
import io.convospot.engine.scheduler.GenericPrecheduler
import io.convospot.util.TestJsonLoader
import org.specs2.Specification
import spray.json._

/**
 * Created by homer on 8/23/15.
 */
class GenericSchedulerSpec extends Specification {

  def is = sequential ^ s2"""
        1. empty if no task & no vehicle   noVechileNoTask
        2. 19 tasks & 5 vehicles   scheduling
        3. Scheduling at the start of the day $schedulingAtStartTheDay
    """

  def noVechileNoTask = {
    val results=GenericPrecheduler.preschedule(List.empty[ImmutableVehicleState],List.empty[Task])
    results must empty
  }

  def scheduling = {
    val testVehicleState= TestJsonLoader.load("vehiclesJSON_1.json").parseJson
    val testOneTask= TestJsonLoader.load("tasksJSON_2.json").parseJson
    val vs=testVehicleState.convertTo[List[ImmutableVehicleState]]
    val tsk=testOneTask.convertTo[List[Task]]
    //Current Time
    val results=GenericPrecheduler.preschedule(vs,tsk,1441300000)
    results.size should be >=0

  }

  def schedulingAtStartTheDay = {
//    val testVehicleState= TestJsonLoader.load("vehiclesJSON_1.json").parseJson
    val testOneTask= TestJsonLoader.load("bodyTask.json").parseJson.toString
//    val vs=testVehicleState.convertTo[List[ImmutableVehicleState]]
//    val tsk=testOneTask.convertTo[List[Task]]
    //Current Time
    val results=ABIScheduler.ABIdataETA(testOneTask,"extranlID",Location(32.869110, -117.218240),1445263867)
    results.size should be >=0

  }


}
