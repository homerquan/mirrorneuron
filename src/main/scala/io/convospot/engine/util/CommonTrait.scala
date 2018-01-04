package io.convospot.engine.util
import io.convospot.engine.config.Config
import io.convospot.engine.grpc.GrpcLauncher.getClass
import org.apache.log4j.Logger

private[convospot] trait CommonTrait {
  val config = Config.apply()
  val log = Logger.getLogger(getClass().getName())
}
