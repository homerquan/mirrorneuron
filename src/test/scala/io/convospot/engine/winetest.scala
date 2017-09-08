package io.convospot.engine

import java.io.{FileWriter, BufferedWriter, File}

import io.convospot.engine.scheduler.{ABIScheduler, Paramet, RoutsFinalS,StopsS}
import io.convospot.importer.{WWI, ABI}
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import spray.json.JsArray

/**
 * Created by Mehrdad on 8/23/2015.
 */
object  winetest {
  def main(args: Array[String]): Unit = {

    val allAssignments=ABIScheduler.WoodWineIdataInputLoader("C:/Users/Mehrdad/Proj/WWI_inputData.xls","GMT-4:00")

    var taskss=WWI.getTasks(allAssignments(0))
    println(taskss(0))

    for(i<-0 to allAssignments.size - 1) {
      // 1. get tasks
      var tasks = WWI.getTasks(allAssignments(i))

      val finalResult = "[" + tasks.mkString(",") + "]"
      val file = new File("C:\\Users\\Mehrdad\\Proj\\Task.json")
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(finalResult)
      bw.close()

    }

    for(i<-0 to allAssignments.size - 1) {
      // 1. get tasks
    var tasks=WWI.getCustomer(allAssignments(i))
      WWI.postingCustomer("C:/Users/Mehrdad/Proj/WWI_inputData.xls","http://localhost:8002/", "Mehrdad","GMT-4:00")

       println(tasks)

      val tasksa=WWI.getCustomer(allAssignments(i))

      for(t<-0 to tasksa.size - 1)
     println(tasksa(t))

      }
  }
}