package io.convospot.engine.prediction

/**
 * Created by ali on 5/23/15.
 */

import io.convospot.cep.events.JsonProtocol._
import io.convospot.cep.events.Vehicle
import io.convospot.util.TestJsonLoader
import org.specs2._
import spray.json._

class GeneralPredictionSpec extends Specification {

  def is = sequential ^ s2"""
        Prediction will output results $getPrediction
    """

  def getPrediction = {
    val epochTime: Long = System.currentTimeMillis()
    val v1= TestJsonLoader.load("vehicle_1.json").parseJson.convertTo[Vehicle]
    val v2= TestJsonLoader.load("vehicle_2.json").parseJson.convertTo[Vehicle]
    val vehList=List(v1,v2)
    val vehicleIds=List("d165716e-057f-11e5-a6c0-1697f925ec7b","d165761e-057f-11e5-a6c0-1697f925ec7b")
    val vehicleLocations=vehList.map{v:Vehicle=>v.location}
    val results=GenericPrediction.suggestDeployment(vehicleIds, vehList,epochTime)
    results must empty
  }
}

