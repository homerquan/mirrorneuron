package io.convospot.engine.app

import io.grpc.ManagedChannelBuilder
import io.convospot.engine.grpc.output.{Request, Response, CommandsGrpc}
import io.convospot.engine.util.Config

private[convospot] object SystemRecovery extends Config{
  private val apiGrpcHost = config.getString("grpc.api-host")
  private val apiGrpcPort = config.getInt("grpc.api-port")
  private implicit val channel = ManagedChannelBuilder.forAddress(apiGrpcHost, apiGrpcPort).usePlaintext(true).build

  def restore() = {
    val request = Request()
    val blockingStub = CommandsGrpc.blockingStub(channel)
    val reply: Response = blockingStub.ask(request)
    println(reply)
  }
}
