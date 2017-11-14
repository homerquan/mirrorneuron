package io.convospot.engine.grpc

import io.convospot.engine.actors.conversation.UserActor

import scala.concurrent.{ExecutionContext, Future}
import io.grpc.ServerBuilder
import io.convospot.engine.grpc.conversation._
import io.grpc.stub.StreamObserver
import org.apache.log4j._


private[convospot] object GrpcLauncher {
  val log = Logger.getLogger(getClass().getName())
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
      req.`type` match {
        case "create_a_new_bot" => {
          Handlers.createBot(req)
        }
        case _ => {
          val ex = new RuntimeException("unsupported request topic:"+ req.`type`)
          Future.failed(ex)
        }
      }
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
