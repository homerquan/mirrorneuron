package io.convospot.engine.actors.common

import akka.actor.ActorRef

final case class RequestMessage(topic:String,request:Any, options:Map[String,String]=Map.empty[String,String], sender:ActorRef=null)
final case class ResponseMessage(topic:String,response:Any, options:Map[String,String]=Map.empty[String,String], sender:ActorRef=null)
