package io.convospot.engine.carpooling

/**
 * Created by ali on 6/7/15.
 */


object oneVehCarpoolingSpec extends App {

  override def main (args: Array[String] ): Unit = {
    oneVehCarpooling

  }
  def oneVehCarpooling = {

    val vehList=ReadFilesCSV.readVehicles("src/test/resources/vehicles.csv")
    val taskList=ReadFilesCSV.readTasks("src/test/resources/tasks.csv")

    val results=MainCarPooling.runningCarpooling(vehList,taskList)

  }


}



