package io.convospot.engine.actors.context.output

/**
 * Json for output
 */
final case class CreateMessage(helper: String, source: String, text: String, conversation: String, visitor: String)
