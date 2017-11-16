package io.convospot.engine.grpc.data

import spray.json.{DefaultJsonProtocol}


/**
  * JsonProtocol for parse & stringify json objects
  */

private[convospot] object JsonProtocol extends DefaultJsonProtocol {
  implicit val createBotFormat = jsonFormat(CreateBot, "id", "client", "user")
  implicit val createConversationFormat = jsonFormat(CreateConversation, "id", "bot", "client")
}
