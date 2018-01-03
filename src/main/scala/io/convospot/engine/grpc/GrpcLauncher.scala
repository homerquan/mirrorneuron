package io.convospot.engine.grpc

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.conversation._
import io.convospot.engine.constants.Grpc
import org.apache.log4j._


private[convospot] object GrpcLauncher {
  private implicit val log = Logger.getLogger(getClass().getName())

  val server = new GrpcServer(
    ServerBuilder
      .forPort(Grpc.PORT)
      .addService(ConversationGrpc.bindService(new ConversationImpl, ExecutionContext.global))
      .build()
  )

  def start() = {
    server.start()
    server.blockUntilShutdown()
  }

}
