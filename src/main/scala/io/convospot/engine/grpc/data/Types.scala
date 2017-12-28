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
final case class SwitchHelperMode(helper: String, mode: String, bot: String, client: String)
final case class OnlineVisitor(visitor:String,bot: String, client: String)
final case class OfflineVisitor(visitor:String,bot: String, client: String)
final case class CreateUser(id:String,bot: String, client: String)
final case class OnlineUser(user:String,bot: String, client: String)
final case class OfflineUser(user:String,bot: String, client: String)
final case class AddUserToHelper(user:String,helper:String,bot:String,client:String)
final case class FillConversation(conversation: String, bot: String, client: String)
final case class Analytics(eid:String,visitor:String,bot:String,event:String,intention:String,timestamp:Int)

