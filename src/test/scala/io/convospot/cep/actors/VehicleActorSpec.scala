package io.convospot.cep.actors

import org.specs2._


/**
 * Created by homer on 4/30/15.
 */


class VehicleActorSpec extends Specification {

  def is = sequential ^ s2"""
  A VehicleWorkerActor should
    updateLocation                                updateLocation
    """

  def updateLocation = {
      1 must be_==(1)
  }

}