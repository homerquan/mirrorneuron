package io.convospot.engine.actors.context.output

import spray.json.DefaultJsonProtocol

private[convospot] object JsonProtocol extends DefaultJsonProtocol {
  implicit val createMessageFormat = jsonFormat(CreateMessage, "helper", "source", "text", "conversation", "visitor")
}