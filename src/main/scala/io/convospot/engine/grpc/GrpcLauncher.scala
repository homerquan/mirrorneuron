package io.convospot.engine.grpc

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.input.CommandsGrpc
import io.convospot.engine.constants.Grpc
import org.apache.log4j._
import io.convospot.engine.util.Config


private[convospot] object GrpcLauncher extends Config {
  private implicit val log = Logger.getLogger(getClass().getName())

  val server = new GrpcServer(
    ServerBuilder
      .forPort(config.getInt("grpc.port"))
      .addService(CommandsGrpc.bindService(new ConversationImpl, ExecutionContext.global))
      .build()
  )

  def start() = {
    server.start()
    server.blockUntilShutdown()
  }

}
