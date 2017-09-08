package io.convospot.engine.analysis

import io.convospot.engine.scheduler.ABIScheduler
import io.convospot.cep.events.JsonProtocol._
import io.convospot.util.TestJsonLoader
import org.specs2.Specification
import spray.json._

/**
 * Created by homer on 8/23/15.
 */
class RouteAnalysisSpec extends Specification {

  def is = sequential ^ s2"""
        3. Evaluateing the complete tasks $evaluateCompleteAssignments
    """

//  final case class AssignmentsSPEC(id: String, task: String, driver: String, `type`: String, location: String,completedAt:String)

  def evaluateCompleteAssignments = {
    val assignmentsJSON= TestJsonLoader.load("AssignmentsRoute17.json").parseJson.toString
//    val assignmentsJSON= TestJsonLoader.load("AssignmentsRouteCambridge.json").parseJson.toString
    //Current Time
//    val serv=
    val user="27331fd8-2b00-11e5-b345-feff819cdc9f"

    val results=ABIScheduler.ABIassignmentsEvaluation("http://localhost:8002/",user,assignmentsJSON) ///32.869110, -117.218240 //42.361845, -71.086150
    results.size should be >=0

  }



}
