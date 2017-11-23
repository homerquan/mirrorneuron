package io.convospot.engine.grpc.data

final case class CreateBot(id: String, client: String)
final case class CreateVisitor(id: String, bot: String, client: String)
final case class CreateHelper(id: String, bot: String, client: String)
final case class CreateConversation(id: String, bot: String, client: String)
final case class JoinConversation(visitor: String, conversation: String, bot: String, client: String)
final case class LeaveConversation(visitor: String, conversation: String, bot: String, client: String)
final case class SuperviseConversation(helper: String, conversation: String, bot: String, client: String)
final case class UnsuperviseConversation(helper: String, conversation: String, bot: String, client: String)
final case class Say(source: String, sid: String, message: String, conversation: String, bot: String, client: String)
final case class SwitchConversationMode(conversation: String, mode: String, bot: String, client: String)