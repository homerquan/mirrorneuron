package io.convospot.engine.grpc.data

final case class CreateBot(id: String, client: String)
final case class CreateVisitor(id: String, bot: String, client: String)
final case class CreateConversation(id: String, bot: String, client: String)
final case class JoinConversation(visitor: String, conversation: String, bot: String, client: String)
final case class LeaveConversation(visitor: String, conversation: String, bot: String, client: String)
final case class Say(source: String, sid: String, message: String, conversation: String, bot: String, client: String)