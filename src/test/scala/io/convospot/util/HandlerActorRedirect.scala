package io.convospot.util

import io.convospot.cep.actors.handlers.BaseHandlerActor
import io.convospot.cep.events.GenericResponseMessage

/**
 * Redirect HandlerActor's output for unit test
 * Created by homer on 6/26/15.
 */
trait HandlerActorRedirect extends BaseHandlerActor{
  override def output(data: String, options: Map[String,String])= {
    // redirect to test context master for using akka testkit
    context.actorSelection("../")!GenericResponseMessage("wise.internal.response.unit_test", data, options)
  }
}
