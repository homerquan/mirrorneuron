package io.convospot.engine.grpc.data

final case class CreateBot(id: String, client: String, user: String)