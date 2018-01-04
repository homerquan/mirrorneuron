package io.convospot.engine.util
import io.convospot.engine.config.Config

trait Config {
  val config = Config.apply()
}
