package io.convospot.cep.actors

import org.specs2._

/*
 * A Behavior spec. Use it as the document to describe specs from business aspect
 */

class  CommandReceiverActorSpec extends Specification { def is = s2"""
  A CommandReceiverActor should
    The command receiver should receive comamands from MQ        $ok
    And send outcome into MQ              $ok
    and bababa
  """
}