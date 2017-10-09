package io.convospot.engine.app

import akka.actor.Props
import com.typesafe.scalalogging.LazyLogging
import io.convospot.engine.config.Config
import scala.util.Properties.{javaVersion, javaVmName, versionString}


/**
  * Launch point of convospot engine
  *
  * @author homer quan
  *
  *         It starts two pipeline:
  * 1. Event stream processing
  * 2. Command RPC processing
  */
object Main extends App with LazyLogging {

  override def main(args: Array[String]) {
    printWelcome()
    //    //CEP
    //    EventGraph.start()
    //    //RPC
    //    CommandGraph.start()
  }

  //TODO: close gracefully (close actors first)

  private def echo(msg: String) {
    Console println msg
  }

  // Print a welcome message and env info
  private def printWelcome() {
    echo(
      """                         _                                                                            _
              ____ ___  (_)_____________  _________  ___  __  ___________  ____     ___  ____  ____ _(_)___  ___
             / __ `__ \/ / ___/ ___/ __ \/ ___/ __ \/ _ \/ / / / ___/ __ \/ __ \   / _ \/ __ \/ __ `/ / __ \/ _ \
            / / / / / / / /  / /  / /_/ / /  / / / /  __/ /_/ / /  / /_/ / / / /  /  __/ / / / /_/ / / / / /  __/
           /_/ /_/ /_/_/_/  /_/   \____/_/  /_/ /_/\___/\__,_/_/   \____/_/ /_/   \___/_/ /_/\__, /_/_/ /_/\___/
                                                                                            /____/
""")
    val welcomeMsg = "Using Scala %s (%s, Java %s) ".format(
     versionString, javaVmName, javaVersion)
    echo(welcomeMsg)
    logger.debug("Settings: " + Config.apply().toString)
  }

}