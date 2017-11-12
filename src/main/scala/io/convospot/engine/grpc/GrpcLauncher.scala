package io.convospot.engine.grpc

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.conversation._
import io.grpc.stub.StreamObserver
import java.util.logging.Logger


private[convospot] object GrpcLauncher {

  private class ConversationImpl extends ConversationGrpc.Conversation {
    override def say(req: EventRequest) = {
      val reply = EventResponse(message = "Hello " + req.message)
      Future.successful(reply)
    }

    /*
    * @param responseObserver an observer to receive the stream of previous messages.
    * @return an observer to handle requested message/location pairs.
     */
    override def talk(responseObserver: StreamObserver[EventResponse]): StreamObserver[EventRequest] = {
      new StreamObserver[EventRequest]() {
        override def onNext(note: EventRequest): Unit = {
        }

        override def onError(t: Throwable): Unit = {
        }

        override def onCompleted(): Unit = {
          responseObserver.onCompleted
        }
      }
    }

    override def join(req: CommandRequest) = {
      val reply = CommandResponse(message = "Hello " + req.message)
      Future.successful(reply)
    }

    override def leave(req: CommandRequest) = {
      val reply = CommandResponse(message = "Hello " + req.message)
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
