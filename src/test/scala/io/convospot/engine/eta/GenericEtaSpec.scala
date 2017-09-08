//package io.wisesystems.engine.eta
//
///**
// * Created by homer on 6/15/15.
// */
//import io.wisesystems.cep.events.Location
//import org.specs2._
//
//class GenericEtaSpec extends Specification {
//
//  def is = sequential ^ s2"""
//       Eta will output 0 for same address $getZero
//       Eta will output > 100 for a long distance $getValue
//       Eta will output > ERROR for out of range $getError
//    """
//
//  def getZero = {
//    val results=GenericEta.getEta(Location(48.87183,2.35925),Location(48.87183,2.35925))
//
//    println(results.get("status").toString+"  "+results.get("traveltimes").toString)
//    results must not be empty
//  }
//
//  def getValue = {
//    val results=GenericEta.getEta(Location(48.87283,2.35925),Location(48.87183,2.75825))
//    println(results.get("status").toString+"  "+results.get("traveltimes").toString)
//    results must not be empty
//  }
//  def getError = {
//    val results=GenericEta.getEta(Location(49.690739, -0.185503),Location(49.555515,-0.473120))
//    println(results.get("status").toString+"  "+results.get("traveltimes").toString)
//    results must not be empty
//  }
//}