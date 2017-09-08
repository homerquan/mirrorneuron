package io.convospot.util

import scala.io.Source

/**
 * Load mockups or test cases from resources
 * Created by homer on 5/1/15.
 */
private[convospot] object TestJsonLoader {
  def load(fileName:String):String = {
    try {
      println(getClass.getResource("/"+fileName))
      Source.fromURL(getClass.getResource("/"+fileName)).getLines.mkString
    } catch {
      case ex: Exception => ???
    }
  }
}