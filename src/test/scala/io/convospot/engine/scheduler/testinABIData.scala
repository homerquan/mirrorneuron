package io.convospot.engine.scheduler

import io.convospot.engine.scheduler.schConf
import io.convospot.importer.ABI
import io.convospot.cep.events._

/**
 * Created by ali on 8/18/15.
 */
object testinABIData extends App{

  var tokLogIn=""

  override def main (args: Array[String]): Unit = {

    val apiServer = "http://localhost:8002/"
    //schConf.workingDrectory
//    ABI.postingTask(schConf.workingDrectory+ "route18-10192015.xls", apiServer, "27331fd8-2b00-11e5-b345-feff819cdc9f", "GMT-7:00")
    ABI.assigningTask(apiServer, Location(32.869110, -117.218240), System.currentTimeMillis() / 1000, "27331fd8-2b00-11e5-b345-feff819cdc9f") //(par.formatDateABI.parse("8/20/15 1:00")).getTime/1000

  }


  }
