package io.convospot.engine.grpc

import io.convospot.engine.grpc.conversation.{ConversationGrpc, Request, Response}
import io.grpc.stub.StreamObserver

import scala.concurrent.Future

private[convospot] class ConversationImpl extends ConversationGrpc.Conversation {
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
      case "join_a_conversation" => {
        Handlers.createBot(req)
      }
      case "end_a_conversation" => {
        Handlers.createBot(req)
      }
      case "reset_and_seed_the_engine" => {
        Handlers.createBot(req)
      }
      case _ => {
        val ex = new RuntimeException("unsupported request topic:" + req.`type`)
        Future.failed(ex)
      }
    }
  }
}
