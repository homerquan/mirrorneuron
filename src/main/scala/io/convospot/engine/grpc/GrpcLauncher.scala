package io.convospot.engine.grpc

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.input.CommandsGrpc
import io.convospot.engine.constants.GrpcOutputCode
import org.apache.log4j._
import io.convospot.engine.util.ObjectTrait


private[convospot] object GrpcLauncher extends ObjectTrait {

  val server = new GrpcServer(
    ServerBuilder
      .forPort(config.getInt("grpc.port"))
      .addService(CommandsGrpc.bindService(new ConversationImpl, ExecutionContext.global))
      .build()
  )

  def start() = {
    log.debug("Grpc server is ready.")
    server.start()
    server.blockUntilShutdown()
  }

}
