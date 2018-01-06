package io.convospot.engine.util

import akka.util.Timeout
import io.convospot.engine.constants.Timeouts
import io.convospot.engine.grpc.output.CommandsGrpc
import io.grpc.ManagedChannelBuilder

private[convospot] object GrpcApiConnector extends CommonTrait {
  private implicit val shortTimeout = Timeout(Timeouts.SHORT)
  private implicit val apiGrpcHost = config.getString("grpc.api-host")
  private implicit val apiGrpcPort = config.getInt("grpc.api-port")
  private implicit val channel = ManagedChannelBuilder.forAddress(apiGrpcHost, apiGrpcPort).usePlaintext(true).build
  val blockingStub = CommandsGrpc.blockingStub(channel)
}
