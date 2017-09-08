package io.convospot.engine.geo

import io.convospot.cep.events.Location
import org.specs2._

/**
 * Created by homer on 7/28/15.
 */
class GeoUtilsSpec extends Specification {
  def is = sequential ^ s2"""
        get distance $getDistance
    """

  def getDistance = {
    val src=Location(42.362153,-71.08588)
    val dest=Location(42.382153,-71.28588)
    val results=GeoUtils.getDistance(src,dest)
    results must >= (0)
  }
}
