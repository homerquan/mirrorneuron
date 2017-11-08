package io.convospot.engine.grpc

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.conversation.{GreeterGrpc, HelloRequest, HelloResponse}
import io.grpc.stub.StreamObserver



private[convospot] object GrpcLauncher {

  private class GreeterImpl extends GreeterGrpc.Greeter {
    override def sayHello(req: HelloRequest) = {
      val reply = HelloResponse(message = "Hello " + req.name)
      Future.successful(reply)
    }
  }

  val server = new GrpcServer(
    ServerBuilder
      .forPort(8980)
      .addService(GreeterGrpc.bindService(new GreeterImpl, ExecutionContext.global))
      .build()
  )

  def start() = {
    server.start()
    server.blockUntilShutdown()
  }

}
