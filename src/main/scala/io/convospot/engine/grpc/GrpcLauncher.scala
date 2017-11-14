package io.convospot.engine.grpc

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.conversation._
import io.grpc.stub.StreamObserver


private[convospot] object GrpcLauncher {

  private class ConversationImpl extends ConversationGrpc.Conversation {
    override def say(req: Request) = {
      val reply = Response(message = "Hello " + req.message)
      Future.successful(reply)
    }

    /*
     * @param responseObserver an observer to receive the stream of previous messages.
     * @return an observer to handle requested message/location pairs.
     */
    override def talk(responseObserver: StreamObserver[Response]): StreamObserver[Request] = {
      new StreamObserver[Request]() {
        override def onNext(note: Request): Unit = {
        }

        override def onError(t: Throwable): Unit = {
        }

        override def onCompleted(): Unit = {
          responseObserver.onCompleted
        }
      }
    }

    override def ask(req: Request) = {
      val reply = Response(message = "Hello " + req.message)
      Future.successful(reply)
    }
  }

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
