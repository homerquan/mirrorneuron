package io.convospot.engine.scheduler

import java.io.File
import java.util.{Random, TimeZone}

import io.convospot.cep.events.Location
import io.convospot.engine.eta.GridTimeZoneAPI
import io.convospot.engine.scheduler._

/**
 * Created by ali on 6/7/15.
 */

  object MainPreSchedulerSpec extends App{



  override def main (args: Array[String]) {


      val vehIDRand: Random = new Random
      val par:Paramet=new Paramet


      /////////////////////////Reading from CSV file
//    val allAssignments=ABIScheduler.WoodWineIdataInputLoader(schConf.workingDrectory+"woodberry_932015-2.xls","GMT-4:00")
//    val js=WWI.getTasks(allAssignments(0))

      var reader = com.github.tototoshi.csv.CSVReader.open(new File(schConf.workingDrectory+"tasks_s.csv"))
      val dataTask=reader.all()
      reader = com.github.tototoshi.csv.CSVReader.open(new File(schConf.workingDrectory+"vehicles_s.csv"))
      val dataVeh=reader.all()
      reader.close()
      var M: Int = dataVeh.size-1
      par.currRouts= Array.ofDim[RoutsFinalS](M)

      for(i<-1 to M) {
        par.currRouts(i-1)=new RoutsFinalS
        par.currRouts(i-1).setVehicleID(dataVeh(i)(0))
        par.currRouts(i-1).setLatStart(dataVeh(i)(1).toDouble)
        par.currRouts(i-1).setLngStart(dataVeh(i)(2).toDouble)

        par.currRouts(i-1).setInitialTime(par.stnFormat.parse("2015-05-11T00:00:00z")) //todo initial time


        var count=0
        var listStart: List[Long]=Nil
        var listEnds: List[Long]=Nil
        while (dataVeh(i)(3+count).length>2) {
          listStart = listStart ::: List(par.stnFormat.parse(dataVeh(i)(3 + count).toString).getTime)
          listEnds = listEnds ::: List(par.stnFormat.parse(dataVeh(i)(4 + count).toString).getTime)
          count += 2
        }
        par.currRouts(i-1).setShiftTimeStr(listStart.toArray)
        par.currRouts(i-1).setShiftTimeEnd(listEnds.toArray)

        listEnds=List()
        listStart=List()
      }

      //TODO Save task ID
      var N: Int = dataTask.size-1
      par.allStops= Array.ofDim[StopsS](N)
      for(i<-1 to N){
        par.allStops(i-1)=new StopsS
        par.allStops(i-1).setTaskId(dataTask(i)(0))
        par.allStops(i-1).setLatPickup(dataTask(i)(1).toDouble)
        par.allStops(i-1).setLngPickup(dataTask(i)(2).toDouble)
        par.allStops(i-1).setLatDelivery(dataTask(i)(4).toDouble)
        par.allStops(i-1).setLngDelivery(dataTask(i)(5).toDouble)
        var pickTime=par.stnFormat.parse(dataTask(i)(3).toString).getTime
        val offsetTime = GridTimeZoneAPI.getTimeZone(Location(par.allStops(i-1).getLatPickup,par.allStops(i-1).getLngPickup),pickTime/1000)
        par.stnFormat.setTimeZone(TimeZone.getTimeZone("GMT"+(offsetTime get "OK").get))
        pickTime=par.stnFormat.parse(dataTask(i)(3).toString).getTime

        par.allStops(i-1).setPickupTime(pickTime)
        par.allStops(i-1).setOpenTWindow(Array(pickTime-par.TimeWin))
        par.allStops(i-1).setCloseTWindow(Array(pickTime))
        par.allStops(i-1).setPickupAddress(dataTask(i)(4) + "|" + dataTask(i)(5))
        par.allStops(i-1).setOpenCloseTime(Array(Long.MinValue, Long.MaxValue))// Disabled Open and closeTime
        par.allStops(i-1).setPriority(1)//TODO
      }
      //how to conver lat long to GTM
      //http://stackoverflow.com/questions/2044876/does-anyone-know-of-a-library-in-java-that-can-parse-esri-shapefiles
   //    getTimeZoneFromLatLng(Location(42.356898, -71.109968))


       val results = GenericPrecheduler.runningPreschedule(par.currRouts,par.allStops)

      //      val gridMatrix=GridMatrixDirectionsAPI

      //      val gridMatrix:GridMatrixDirectionsAPI=new GridMatrixDirectionsAPI
      //      val found = GridMatrixDirectionsAPI.googleDurationRedis(pickUpLoc(0), deliveryLoc(0),serviceTime(0))
      //      println(found)


    }


}



