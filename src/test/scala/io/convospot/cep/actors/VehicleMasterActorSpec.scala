package io.convospot.cep.actors

import akka.actor.Props
import akka.testkit.TestProbe
import io.convospot.cep.events._
import io.convospot.util.{ActorsTestSupport, TestJsonLoader}
import org.specs2.Specification
import spray.json._
import io.convospot.cep.events.JsonProtocol._
import scala.concurrent.duration._

class VehicleMasterActorSpec extends Specification {

  def is = sequential ^ s2"""
  [This spec tests VehicleMasterActor]    ${step(setup)}
  A VehicleMasterActor should response following messages:

    addVehicle  addVehicle

    searchVehicles  searchVehicles

    addTask  addTask

    searchTasks  searchTasks

    removeTask: remove task will cancel its assignments as well   removeTask

    addAssignment addAssignment

    getSchedule getSchedule

    setSchedule setSchedule

    addBreak addBreak

    generateAssignments generateAssignments

   addVehicle Logic:
    should not allow duplicated vechile name uniqueVechileName
    should consider in vehicle capacity in cubic meters         $ok
    should assign small, medium, large designation based on cubic meters    $ok
    should consider vehicle type (car, truck, motorcycle)                    $ok
    should consider indicator of other car capability (e.g. refrigeration)   $ok
    should consider current location as latitude and longitude               $ok
    should consider vehicle availability start time                          $ok
    should consider vehicle availability end time                            $ok
    should consider timewindow to indicate scheduled breaks (lunch, prayers etc.) between start and end times $ok
    should return a confirmation message indicating the successful addition of the vehicle. $ok


    should find all vehicles within a X km radius that meet the requirements of the task
    with query capacity, features(e.g., priority, timewindow) and response in ETA order        $ok  

  addTask Logic:
    should take in the address of the location (number, street, apt (opt), city, state, zipcode $ok
    should take in latitude and longitude of the task location                              $ok
    should take in task type as delivery or pickup                                          $ok
    should take in requested time of task (when should the task be fulfilled)               $ok
    should take in task size in cubic meters                                                $ok
    should take in task size as small, medium, or large                                     $ok
    should take in indicator of other car capability (e.g. refrigeration)                   $ok
    should take in priority of the task (high, medium, low)                                 $ok
    should find all vehicles within a X km radius that meet the requirements of the task    $ok
    if all vehicles and their schedules are full, no ETA is given for the vehicles. The tasks is queued until the next available car becomes available for service. The task can be cancelled by the user or operator. $ok
    
    should return a list of vehicles that meet the criteria and order them based on ETA     $ok

  addAssignment Logic:
    should find the vehicle with the matching truck id that was passed in as a requested task $ok
    should recalculate the ETA for the new tasks                                              $ok
    should recalculate the ETA for other tasks on the schedule with the new task incorporated $ok
    should assign the task to the vehicle and rebuild the schedule                            $ok
    should store the new schedule for the vehicle                                             $ok
    should return a confirmation message indicating the successful assignment of the task     $ok





    """
  //
  //    searchVehicles  $searchVehicles
  //    removeVehicle                              $removeVehicle
  //    removeTask                                 $removeTask
  //    initSchedule                               $initSchedule

  def setup = {}

  def searchVehicles = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = TestProbe()
        val message = GenericRequestMessage("wise.message.request.vehicle.search",null)
        val actor = system.actorOf(Props(classOf[MasterActor]))
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]
      }
    }
  }

  def searchTasks = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = TestProbe()
        val message = GenericRequestMessage("wise.message.request.task.search",null)
        val actor = system.actorOf(Props(classOf[MasterActor]))
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]
      }
    }
  }

  def generateAssignments = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = TestProbe()
        val message = GenericRequestMessage("wise.command.assignment.generate",null)
        val actor = system.actorOf(Props(classOf[MasterActor]))
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]
        // in the format such as
        //        list of vehicles with their assigned tasks
        //          Truck T - 3 tasks (taskid - time)
        //          Car K -  6 tasks
        //          Motorcycle M - 8 tasks

      }
    }
  }

  def addVehicle = {
    new ActorsTestSupport {
      within(5 second) {
        val probe = TestProbe()
        val testCase= TestJsonLoader.load("wise.command.vehicle.add.json").parseJson
        val payload=testCase.convertTo[Vehicle]
        val message = GenericRequestMessage("wise.message.request.vehicle.add",payload)
        val actor = system.actorOf(Props(classOf[MasterActor]),"vehiclemaster")
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]
      }
    }
  }

  def addTask = {
    new ActorsTestSupport {
      within(1 second) {

        val probe = TestProbe()
        val testCase= TestJsonLoader.load("wise.command.vehicle.add.json").parseJson
        val payload=testCase.convertTo[Vehicle]
        val message = GenericRequestMessage("wise.message.request.vehicle.add",payload)
        val actor = system.actorOf(Props(classOf[MasterActor]),"vehiclemaster")
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]

        val probe3 = TestProbe()
        val testCase3= TestJsonLoader.load("trk2.json").parseJson
        val payload3=testCase3.convertTo[Vehicle]
        val message3 = GenericRequestMessage("wise.message.request.vehicle.add",payload3)
        actor.tell(message3, probe3.ref)
        probe3.expectMsgType[GenericResponseMessage]

        val probe2 = TestProbe()
        val testCase2= TestJsonLoader.load("wise.command.task.add.json").parseJson
        val payload2=testCase2.convertTo[Task]
        val message2 = GenericRequestMessage("wise.message.request.task.add",payload2)
        actor.tell(message2, probe2.ref)
        probe2.expectMsgType[GenericResponseMessage]
      }
    }
  }

  def uniqueVechileName = {
    new ActorsTestSupport {
      within(1 second) {
        val probe = TestProbe()
        val testCase= TestJsonLoader.load("wise.command.vehicle.add.json").parseJson
        val payload=testCase.convertTo[Vehicle]
        val message = GenericRequestMessage("wise.command.vehicle.add",payload)
        val actor = system.actorOf(Props(classOf[MasterActor]))
        actor.tell(message, probe.ref)
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]
        probe.expectMsgType[ErrorResponseMessage]
      }
    }
  }


  def removeTask={
    new ActorsTestSupport {
      within(1 second) {
        val probe = TestProbe()
        val message = GenericRequestMessage("wise.command.task.remove","ef623510-f4f1-11e4-b9b2-1697f925ec7b")
        val actor = system.actorOf(Props(classOf[MasterActor]))
        actor.tell(message, probe.ref)
        probe.expectMsg(ErrorResponseMessage(2,"the task does not exist"))
      }
    }
  }

  def addAssignment={
    new ActorsTestSupport {
      within(5 second){


        val probe = TestProbe()
        val testCase= TestJsonLoader.load("wise.command.vehicle.add.json").parseJson
        val payload=testCase.convertTo[Vehicle]
        val message = GenericRequestMessage("wise.message.request.vehicle.add",payload)
        val actor = system.actorOf(Props(classOf[MasterActor]),"vehiclemaster")
        actor.tell(message, probe.ref)
        probe.expectMsgType[GenericResponseMessage]

/*

                val probe2 = TestProbe()
                val testCase2= TestJsonLoader.load("wise.command.task.add.json").parseJson
                val payload2=testCase2.convertTo[Task]
                val message2 = GenericRequestMessage("wise.message.request.task.add",payload2)
                actor.tell(message2, probe2.ref)
                probe2.expectMsgType[GenericResponseMessage]


                        Thread sleep(2000)

                        val probe3 = TestProbe()
                        val testCase3= TestJsonLoader.load("wise.command.assignment.add.json").parseJson
                        val payload3=testCase3.convertTo[Assignment]
                        val message3 = GenericRequestMessage("wise.message.request.assignment.add",payload3)
                        actor.tell(message3, probe3.ref)
                        probe3.expectMsgType[GenericResponseMessage]*/
      }
    }
  }


}