package io.convospot.engine.grpc.data

import spray.json.{DefaultJsonProtocol}


/**
  * JsonProtocol for parse & stringify json objects
  */

private[convospot] object JsonProtocol extends DefaultJsonProtocol {
  implicit val createBotFormat = jsonFormat(CreateBot, "id", "client")
  implicit val createVisitorFormat =  jsonFormat(CreateVisitor, "id", "bot", "client")
  implicit val createHelperFormat =  jsonFormat(CreateHelper, "id", "bot", "client")
  implicit val createConversationFormat = jsonFormat(CreateConversation, "id", "bot", "client")
  implicit val joinConversationFormat = jsonFormat(JoinConversation, "visitor", "conversation", "bot", "client")
  implicit val leaveConversationFormat = jsonFormat(LeaveConversation, "visitor", "conversation", "bot", "client")
  implicit val superviseConversationFormat = jsonFormat(SuperviseConversation, "helper", "conversation", "bot", "client")
  implicit val unsuperviseConversationFormat = jsonFormat(UnsuperviseConversation, "helper", "conversation", "bot", "client")
  implicit val sayFormat = jsonFormat(Say, "source", "sid", "message", "conversation", "bot", "client")
  implicit val switchConversationModeFormat = jsonFormat(SwitchConversationMode, "conversation", "mode","bot", "client")
}
