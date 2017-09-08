package io.convospot.engine.selector

import io.convospot.cep.events._
import io.convospot.engine.OptContainer
import io.convospot.util.TestJsonLoader
import org.specs2.Specification
import spray.json._
import io.convospot.cep.events.JsonProtocol._

/**
 * Created by homer on 7/28/15.
 */
class GenericSelectorSpec extends Specification {
  def is = sequential ^ s2"""
        filter top n closest vehicles $filterVehicles
    """

  def filterVehicles = {
    val testVehicleState= TestJsonLoader.load("immutable_vehicle_state.json").parseJson
    val testTask= TestJsonLoader.load("cep_task_1.json").parseJson
    val vs=testVehicleState.convertTo[ImmutableVehicleState]
    val task = testTask.convertTo[Task]
    val testOpt=new OptContainer with GenericSelector
    val results=testOpt.filterVehicles(List(vs,vs),task,2)
    results.size must_== 2
  }
}
