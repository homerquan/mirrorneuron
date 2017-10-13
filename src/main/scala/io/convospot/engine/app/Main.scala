/*
Launch point of convospot engine
@author homer kwan
*/

package io.convospot.engine.app

import com.typesafe.scalalogging.LazyLogging
import io.convospot.engine.config.Config

import scala.util.Properties.{javaVersion, javaVmName, versionString}
import java.io.File

import io.convospot.engine.console.Launcher

private[convospot] object Main extends App with LazyLogging {

  override def main(args: Array[String]) {

    case class Input(restoreFile: File = new File("."), profile: File = new File("."), verbose: Boolean = false, debug: Boolean = false, console: Boolean = false)

    printWelcome()

    val parser = new scopt.OptionParser[Input]("engine") {

      opt[File]('p', "profile").valueName("<file>").
        action((x, c) => c.copy(profile = x)).
        text("load a profile")

      opt[File]('r', "restore").valueName("<file>").
        action((x, c) => c.copy(restoreFile = x)).
        text("restore from previous data")

      opt[Unit]('c', "console").action((_, c) =>
        c.copy(console = true)).text("interactive console mode")

      opt[Unit]('v', "verbose").action((_, c) =>
        c.copy(verbose = true)).text("show verbose")

      opt[Unit]("debug").hidden().action((_, c) =>
        c.copy(debug = true)).text("enable debug")

      help("help").text("prints this usage text")
    }

    // parser.parse returns Option[C]
    parser.parse(args, Input()) match {
      case Some(config) => {
        if (config.console) {
          Launcher.start()
        }
      }

      case None =>
      // arguments are bad, error message will have been displayed
    }
  }

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