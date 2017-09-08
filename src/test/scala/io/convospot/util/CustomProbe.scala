package io.convospot.util

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import io.convospot.cep.events._

/**
 * Created by homer on 6/30/15.
 */
class CustomProbe(system:ActorSystem) extends TestProbe(system:ActorSystem)  {
  // adding custom probe if necessary here
}
