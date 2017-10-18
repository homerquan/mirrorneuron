package io.convospot.engine.console

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, Props}
import akka.io.Tcp._
import akka.io._
import io.convospot.engine.actors.conversation.UserActor

/**
  * Main TCP socket server (for console test)
  */
class Server(address: String, port: Int) extends Actor with ActorLogging {

  override def preStart() {
    log.info("Starting org.example.chat.Server")

    import context.system
    val opts = List(SO.KeepAlive(on = true), SO.TcpNoDelay(on = true))
    IO(Tcp) ! Bind(self, new InetSocketAddress(address, port), options = opts)
  }

  def receive = {

    case b@Bound(localAddress) =>
      log.info("org.example.chat.Server bound at " + localAddress)

    case CommandFailed(_: Bind) =>
      log.info("Command failed org.example.chat.Server")
      context stop self

    case c@Connected(remote, local) =>
      log.info("New incoming connection on org.example.chat.Server")
      var role = ""
      if (local.getPort == 1981) {
         role = "HELPER"
      } else {
         role = "VISITOR"
      }


      val visitor = context.actorOf(UserActor.props(sender,role))

      visitor ! UserActor.Message.Greeting
      sender ! Register(visitor)

  }

}

object Server {

  def props(address: String, port: Int) = Props(new Server(address, port))

}