package io.convospot.engine.grpc

import java.util.logging.Logger
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.grpc.Server

class GrpcServer(server: Server) {

  val logger: Logger = Logger.getLogger(classOf[GrpcServer].getName)

  def start(): Unit = {
    server.start()
    logger.info(s"gRPC Server started, listening on ${server.getPort}")
    sys.addShutdownHook {
      // Use stderr here since the logger may has been reset by its JVM shutdown hook.
      logger.info("shutting down gRPC server since JVM is shutting down")
      stop()
      logger.info("gRPC server shut down")
    }
    ()
  }

  def stop(): Unit = {
    server.shutdown()
  }

  /**
    * Await termination on the main thread since the grpc library uses daemon threads.
    */
  def blockUntilShutdown(): Unit = {
    server.awaitTermination()
  }

}
