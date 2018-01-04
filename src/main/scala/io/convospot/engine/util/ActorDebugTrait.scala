package io.convospot.engine.util

import akka.actor.ActorSystem
import io.convospot.engine.grpc.GrpcExecutor.system

trait ActorDebugTrait {
  def printActors(system:ActorSystem) = {
    val res = new PrivateMethodExposer(system)('printTree)()
    println(res)
  }
}
