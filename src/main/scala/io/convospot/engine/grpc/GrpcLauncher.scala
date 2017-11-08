package io.convospot.engine.grpc

import io.grpc.ServerBuilder

private[convospot] object GrpcLauncher {

  val server = new GrpcServer(
    ServerBuilder
      .forPort(8980)
      .build()
  )

  def start() = {
    server.start()
    server.blockUntilShutdown()
  }

}
