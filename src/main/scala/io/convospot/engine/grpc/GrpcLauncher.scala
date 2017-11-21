package io.convospot.engine.grpc

import io.convospot.engine.actors.context.UserActor

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.conversation._
import io.grpc.stub.StreamObserver
import org.apache.log4j._


private[convospot] object GrpcLauncher {
  val log = Logger.getLogger(getClass().getName())

  val server = new GrpcServer(
    ServerBuilder
      .forPort(8980)
      .addService(ConversationGrpc.bindService(new ConversationImpl, ExecutionContext.global))
      .build()
  )

  def start() = {
    server.start()
    server.blockUntilShutdown()
  }

}
