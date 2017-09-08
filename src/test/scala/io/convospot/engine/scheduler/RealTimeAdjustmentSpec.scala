package io.convospot.engine.scheduler

//package io.wisesystems.cep.events

//import io.wisesystems.cep.events._

import java.io.File
import java.sql.Date

import io.convospot.engine.scheduler._

import scala.collection.mutable.ListBuffer

//import com.github.tototoshi.csv.CSVReader
/**
 * Created by ali on 7/17/15.
 */
object RealTimeAdjustmentSpec extends App {

  override def main (args: Array[String]) {

    //  testingSimulation()
      testingGenericRScheduling()
//     testingGenericWarning
//    testingABIData()

  }

  def testingGenericWarning()= {

    var curRts = Array.ofDim[RoutsFinalS](99)
    var stops = Array.ofDim[StopsS](99)
    var allStops = Array.ofDim[StopsS](999)
    val par:Paramet=new Paramet
    var vehicleLoc:String="32.7092285,-117.1682281"
    val reader = com.github.tototoshi.csv.CSVReader.open(new File(schConf.workingDrectory+"Data_2D83_0526.csv"))
    val data=reader.all(); reader.close()

    /////////////////////////Reading from CSV file///////////////
    val LocID_AverageSrvTime=calHistServTime(data)
    var N: Int = 0
    var NRouts: Int = 0

    for(i<-2 to data.size-1){
      if (!curRts(NRouts).isInstanceOf[RoutsFinalS]) {
        curRts(NRouts) = new RoutsFinalS
        curRts(NRouts).setVehicleID(data(i)(6).toString)
        curRts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString)) //todo initial time
      } else if(!curRts(NRouts).getVehicleID.contains(data(i)(6).toString)) {

        curRts(NRouts).setStops(stops)
        val tmp = vehicleLoc.split(",")
        curRts(NRouts).setLatStart(tmp(0).toDouble)   //TODO every vehicle has vehicleID and Initial Time as current Time, vehicle location
        curRts(NRouts).setLngStart(tmp(1).toDouble)

        if (curRts(NRouts).getStopNum > -1) curRts(NRouts).setStopNum(N)

        ///////////////////////////////////////////////////////////////////////////////////////
        //        val warningResults=MainPreSchd.mainRealTimeWarningMethod(Array(curRts(NRouts)), allStops)
        //         println(FunctionsVRPs.tage(warningResults,false))
        val results2 = MainPreSchd.mainRealTimeWarningMethod(Array(curRts(NRouts)), allStops)
        println("Code: "+results2._1)
        println("List: "+results2._2)

        for (j <- 0 to results2._3(0).getStopNum - 1) {
          println("AssigID " + results2._3(0).getStops(j).getTaskId + " UpdatedDelivery: " + par.zformat.format(new Date(results2._3(0).getStops(j).getArrivalTime.getTime)))
        }
        System.exit(0)
        ////////////////////////////////////////////////////////////////////////////////

        stops = Array.ofDim[StopsS](99)
        allStops = Array.ofDim[StopsS](99)
        N = 0
        NRouts += 1
        curRts(NRouts) = new RoutsFinalS
        curRts(NRouts).setVehicleID(data(i)(6).toString)
        curRts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString))
      }

      stops(N)= new StopsS
      allStops(N)= new StopsS

      allStops(N).setTaskId(N.toString+"RealID") //todo this is the exact ID
      stops(N).setTaskId(N.toString)  //TODO This should be the order

      data(i)(4).toString.trim match {//TODO Priority
        case "B" => allStops(N).setPriority(1)
        case "U" => allStops(N).setPriority(2)
        case "D" => allStops(N).setPriority(3)
      }

      val pickUpTime=par.formatDate.parse(data(i)(2).toString).getTime //TODO openandCloseTime
      allStops(N).setOpenCloseTime(Array(pickUpTime-3*24*60*60*1000, pickUpTime+3*24*60*60*1000))

      if(data(i)(12).length>2) { //TODO ActualArrivalTime
        allStops(N).setActualArrivalTime(par.formatDate.parse(data(i)(12).toString).getTime)
      }else{curRts(NRouts).setStopNum(-1)}


      var open:List[Long]=Nil
      var close:List[Long]=Nil
      if(data(i)(16).length>2){ //TODO TimeWindow 1
        open=open:::List(par.formatDate.parse(data(i)(16).toString).getTime)
        close=close:::List(par.formatDate.parse(data(i)(17).toString).getTime)
      }else{
        open=open:::List(allStops(N).getOpenCloseTime(0))
        close=close:::List(allStops(N).getOpenCloseTime(1))
      }

      if(data(i)(18).length>2) {//TODO TimeWindow 2
        open = open ::: List(par.formatDate.parse(data(i)(18).toString).getTime)
        close = close ::: List(par.formatDate.parse(data(i)(19).toString).getTime)
      }

      if(data(i)(7).length>1 && data(i)(8).length>1 && LocID_AverageSrvTime.contains(data(i)(28).toString)) {
        allStops(N).setServiceTime(schConf.defaultServiceTime) //TODO Actual ServiceTime
        stops(N).setServiceTime(schConf.defaultServiceTime)        //TODO Planned ServiceTime
      }else curRts(NRouts).setStopNum(-1)

      allStops(N).setOpenTWindow(open.toArray);open=List()
      allStops(N).setCloseTWindow(close.toArray); close=List()

      if(!data(i)(29).contains("N/A")) {
        allStops(N).setPickupAddress(data(i)(29)) //TODO PickupAddress
        allStops(N).setPickupCity(data(i)(30))
        allStops(N).setLocationID(data(i)(28))                        //TODO PickupLocationID
//        HereAPIs.callGeocod(allStops(N).getPickupAddress.replaceAll("[\\s+()]", "+"), par.workingDrectory)
        HereAPIs.callGeocod(allStops(N).getPickupAddress.replaceAll("[\\s+()]", "+")
          +"+"+allStops(N).getPickupCity.replaceAll("[\\s+()]", "+"), schConf.workingDrectory)

        val outputS: String = HereAPIs.readGeocoding(null, schConf.workingDrectory)
        val token: Array[String] = outputS.split(",")
        if (token(0).length > 2) {
          allStops(N).setLatPickup(token(0).toDouble)
          allStops(N).setLngPickup(token(1).toDouble)
          allStops(N).setLatDelivery(token(0).toDouble)
          allStops(N).setLngDelivery(token(1).toDouble)
        }else N -=1//remove the current stop

      }else N -=1//remove the current stop
      N += 1
    }
    //TODO every vehicle has vehicleID and Initial Time as current Time
    //todo The last route!!#################################
    curRts(NRouts).setStops(stops)                                       //TODO attached stops with planed service and stopID
    if (curRts(NRouts).getStopNum > -1) curRts(NRouts).setStopNum(N)     //TODO the Number of stops
    val tmp=vehicleLoc.split(",")
    curRts(NRouts).setLatStart(tmp(0).toDouble)                           //TODO starting LocationTime
    curRts(NRouts).setLngStart(tmp(1).toDouble)
    //todo The last route!!#################################


    val results2 = MainPreSchd.mainRealTimeWarningMethod(Array(curRts(NRouts)),allStops)
    println("Code: "+results2._1)
    println("List: "+results2._2)

    for (j <- 0 to results2._3(0).getStopNum - 1) {
      println("AssigID " + results2._3(0).getStops(j).getTaskId + " UpdatedDelivery: " + par.zformat.format(new Date(results2._3(0).getStops(j).getArrivalTime.getTime)))
    }


    }

  def testingGenericRScheduling()= {
    val par:Paramet=new Paramet

    par.currRouts = Array.ofDim[RoutsFinalS](99)
    var stops = Array.ofDim[StopsS](99)
    par.allStops = Array.ofDim[StopsS](999)
    var vehicleLoc:String="42.361845, -71.086150" //42.361845, -71.086150 //32.7092285,-117.1682281
    val reader = com.github.tototoshi.csv.CSVReader.open(new File(schConf.workingDrectory+"Data_CambrDataSet.csv"))
    val data=reader.all(); reader.close()

    /////////////////////////Reading from CSV file
    val LocID_AverageSrvTime=calHistServTime(data)
    var N: Int = 0
    var NRouts: Int = 0


    for(i<-2 to data.size-1){
      if (!par.currRouts(NRouts).isInstanceOf[RoutsFinalS]) {
        par.currRouts(NRouts) = new RoutsFinalS
        par.currRouts(NRouts).setVehicleID(data(i)(6).toString)
        par.currRouts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString)) //todo initial time
      } else if(!par.currRouts(NRouts).getVehicleID.contains(data(i)(6).toString)) {

        par.currRouts(NRouts).setStops(stops)
        val tmp = vehicleLoc.split(",")
        par.currRouts(NRouts).setLatStart(tmp(0).toDouble)   //TODO every vehicle has vehicleID and Initial Time as current Time, vehicle location
        par.currRouts(NRouts).setLngStart(tmp(1).toDouble)

        if (par.currRouts(NRouts).getStopNum > -1) par.currRouts(NRouts).setStopNum(N)

        ///////////////////////////////////////////////////////////////////////////////////////
        //        val warningResults=MainPreSchd.mainRealTimeWarningMethod(Array(curRts(NRouts)), allStops)
        //         println(FunctionsVRPs.tage(warningResults,false))
        val results2 = MainPreSchd.mainGeneralRealTimeAdj(par)
        println("RealTimeDj Finished.")
        System.exit(0)
        ////////////////////////////////////////////////////////////////////////////////

        stops = Array.ofDim[StopsS](99)
        par.allStops = Array.ofDim[StopsS](99)
        N = 0
        NRouts += 1
        par.currRouts(NRouts) = new RoutsFinalS
        par.currRouts(NRouts).setVehicleID(data(i)(6).toString)
        par.currRouts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString))
      }

      stops(N)= new StopsS
      par.allStops(N)= new StopsS

      par.allStops(N).setTaskId(N.toString+"RealID") //todo this is the exact ID
      stops(N).setTaskId(N.toString)  //TODO This should be the order

      data(i)(4).toString.trim match {//TODO Priority
        case "B" => par.allStops(N).setPriority(1)
        case "U" => par.allStops(N).setPriority(2)
        case "D" => par.allStops(N).setPriority(3)
      }

      val pickUpTime=par.formatDate.parse(data(i)(2).toString).getTime //TODO openandCloseTime
      par.allStops(N).setOpenCloseTime(Array(pickUpTime-3*24*60*60*1000, pickUpTime+3*24*60*60*1000))

      if(data(i)(12).length>2) { //TODO ActualArrivalTime
        par.allStops(N).setActualArrivalTime(par.formatDate.parse(data(i)(12).toString).getTime)
      }//else{curRts(NRouts).setStopNum(-1)}


      var open:List[Long]=Nil
      var close:List[Long]=Nil
      if(data(i)(16).length>2){ //TODO TimeWindow 1
        open=open:::List(par.formatDate.parse(data(i)(16).toString).getTime)
        close=close:::List(par.formatDate.parse(data(i)(17).toString).getTime)
      }else{
        open=open:::List(par.allStops(N).getOpenCloseTime(0))
        close=close:::List(par.allStops(N).getOpenCloseTime(1))
      }

      if(data(i)(18).length>2) {//TODO TimeWindow 2
        open = open ::: List(par.formatDate.parse(data(i)(18).toString).getTime)
        close = close ::: List(par.formatDate.parse(data(i)(19).toString).getTime)
      }

      if(data(i)(7).length>0 ) {

        par.allStops(N).setServiceTime(data(i)(7).toString.toLong*60*1000) //TODO Actual ServiceTime
        stops(N).setServiceTime(data(i)(7).toString.toLong*60*1000)        //TODO Planned ServiceTime

      }else par.currRouts(NRouts).setStopNum(-1)

      par.allStops(N).setOpenTWindow(open.toArray);open=List()
      par.allStops(N).setCloseTWindow(close.toArray); close=List()

      if(!data(i)(29).contains("N/A")) {
        par.allStops(N).setPickupAddress(data(i)(29)) //TODO PickupAddress
        par.allStops(N).setPickupCity(data(i)(30))

        par.allStops(N).setLocationID(data(i)(28))                        //TODO PickupLocationID
//        HereAPIs.callGeocod(allStops(N).getPickupAddress.replaceAll("[\\s+()]", "+"), par.workingDrectory)
        HereAPIs.callGeocod(par.allStops(N).getPickupAddress.replaceAll("[\\s+()]", "+")
          +"+"+par.allStops(N).getPickupCity.replaceAll("[\\s+()]", "+"), schConf.workingDrectory)

        val outputS: String = HereAPIs.readGeocoding(null, schConf.workingDrectory)
        val token: Array[String] = outputS.split(",")
        if (token(0).length > 2) {
          par.allStops(N).setLatPickup(token(0).toDouble)
          par.allStops(N).setLngPickup(token(1).toDouble)
          par.allStops(N).setLatDelivery(token(0).toDouble)
          par.allStops(N).setLngDelivery(token(1).toDouble)
        }else N -=1//remove the current stop

      }else N -=1//remove the current stop
      N += 1
    }
    //TODO every vehicle has vehicleID and Initial Time as current Time
    //todo The last route!!#################################
    par.currRouts(NRouts).setStops(stops)                                       //TODO attached stops with planed service and stopID
    if (par.currRouts(NRouts).getStopNum > -1)
      par.currRouts(NRouts).setStopNum(N)     //TODO the Number of stops
    val tmp=vehicleLoc.split(",")
    par.currRouts(NRouts).setLatStart(tmp(0).toDouble)                           //TODO starting LocationTime
    par.currRouts(NRouts).setLngStart(tmp(1).toDouble)
    //todo The last route!!#################################

    val results2 = MainPreSchd.mainGeneralRealTimeAdj(par)
    println("RealTimeDj Finished.")
    System.exit(0)

//    var results:List[ScheduleItem]=Nil
//    for(j<-0 to results2(0).getStops.size -1)
//      results = results ::: List(ScheduleItem(results2(0).getStops(j).getTaskId.toString, results2(0).getStops(j).getArrivalTime.getTime, 0))



  }

  def testingSimulation()={

    var curRts = Array.ofDim[RoutsFinalS](999999)
    var stops = Array.ofDim[StopsS](99)
    var allStops = Array.ofDim[StopsS](999)
    val par:Paramet=new Paramet
    var vehicleLoc:String="32.7092285,-117.1682281"
    val reader = com.github.tototoshi.csv.CSVReader.open(new File(schConf.workingDrectory+"Data_2D83_0526.csv"))
    val data=reader.all()
    reader.close()

    ////////////////////////////
    /////////////////////////Reading from CSV file
    val LocID_AverageSrvTime=calHistServTime(data)
    var N: Int = 0
    var NRouts: Int = 0
    var S: Int = 0



    for(i<-2 to data.size-1){
      if (!curRts(NRouts).isInstanceOf[RoutsFinalS]) {

        curRts(NRouts) = new RoutsFinalS
        curRts(NRouts).setVehicleID(data(i)(6).toString)
        curRts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString)) //todo initial time

      } else if(!curRts(NRouts).getVehicleID.contains(data(i)(6).toString)) {

        curRts(NRouts).setStops(stops)
        val tmp = vehicleLoc.split(",")
        curRts(NRouts).setLatStart(tmp(0).toDouble)   //TODO every vehicle has vehicleID and Initial Time as current Time, vehicle location
        curRts(NRouts).setLngStart(tmp(1).toDouble)

        if (curRts(NRouts).getStopNum > -1) curRts(NRouts).setStopNum(S)

        //          val startOfDay = Math.floor(allStops(0).getOpenCloseTime(0) / (24 * 60 * 60 * 1000)).toLong * 24 * 60 * 60 * 1000
        //startOfDay + 6 * 60 * 60 * 1000 + 0 * 60 * 1000 + 0 * 1000
        ///////////////////////////////////////////////////////////////////////////////////////
        val results2 = MainPreSchd.mainRealTimeAdj(Array(curRts(NRouts)), allStops)
        ////////////////////////////////////////////////////////////////////////////////
        stops = Array.ofDim[StopsS](99)
        allStops = Array.ofDim[StopsS](99)
        S = 0
        N = 0
        NRouts += 1
        curRts(NRouts) = new RoutsFinalS
        curRts(NRouts).setVehicleID(data(i)(6).toString)
        curRts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString))
      }




      stops(S)= new StopsS
      allStops(N)= new StopsS

      allStops(N).setTaskId(S.toString)
      stops(S).setTaskId(S.toString)  //TODO TaskID
      //          allStops(N).setTaskId(tokens(0).toString.toInt)
      //          stops(S).setTaskId(tokens(0).toString.toInt)

      data(i)(4).toString.trim match {//TODO Priority
        case "B" =>
          allStops(N).setPriority(1)
        case "U" =>
          allStops(N).setPriority(2)
        case "D" =>
          allStops(N).setPriority(3)
      }

      //          pickUpLoc = pickUpLoc ::: List(tokens(1).toString + "," + tokens(2))
      //          val servTim: Date = formatDate.parse(tokens(5).toString)
      //          serviceTime = serviceTime ::: List(servTim.getTime)
      if(data(i)(14).length>2) {//TODO OpenAndCloseTime
        allStops(N).setOpenCloseTime(Array(par.formatDate.parse(data(i)(14).toString).getTime, par.formatDate.parse(data(i)(15).toString).getTime))
      }else
      {
        curRts(NRouts).setStopNum(-1)
        allStops(N).setOpenCloseTime(Array(par.formatDate.parse(data(i)(2).toString).getTime, par.formatDate.parse(data(i)(2).toString).getTime+ 24 * 60 * 60 * 1000))

      }

      if(data(i)(12).length>2) { //TODO ActualArrivalTime
        allStops(N).setActualArrivalTime(par.formatDate.parse(data(i)(12).toString).getTime)
      }else{
        curRts(NRouts).setStopNum(-1)
      }

      var open:List[Long]=Nil
      var close:List[Long]=Nil

      if(data(i)(16).length>2){ //TODO TimeWindow 1
        open=open:::List(par.formatDate.parse(data(i)(16).toString).getTime)
        close=close:::List(par.formatDate.parse(data(i)(17).toString).getTime)

      }else {
        open=open:::List(allStops(N).getOpenCloseTime(0))
        close=close:::List(allStops(N).getOpenCloseTime(1))
      }

      if(data(i)(18).length>2) {//TODO TimeWindow 2
        open = open ::: List(par.formatDate.parse(data(i)(18).toString).getTime)
        close = close ::: List(par.formatDate.parse(data(i)(19).toString).getTime)
      }

      if(data(i)(7).length>1 && data(i)(8).length>1 && LocID_AverageSrvTime.contains(data(i)(28).toString)) {
        allStops(N).setServiceTime(Math.floor(data(i)(7).toString.toDouble * 60 * 60 * 1000).toLong) //TODO Actual ServiceTime
        //stops(S).setServiceTime(Math.floor(tokens(8).toString.toDouble * 60 * 60 * 1000).toLong)
        stops(S).setServiceTime(LocID_AverageSrvTime.get(data(i)(28).toString).get)        //TODO Planned ServiceTime

      }else
        curRts(NRouts).setStopNum(-1)

      allStops(N).setOpenTWindow(open.toArray);open=List()
      allStops(N).setCloseTWindow(close.toArray); close=List()


      //        serviceTime = serviceTime ::: List(tokens(3).toLong)
      if(!data(i)(29).contains("N/A")) {
        allStops(N).setPickupAddress(data(i)(29)) //TODO PickupAddress
        allStops(N).setPickupCity(data(i)(30))
        allStops(N).setLocationID(data(i)(28))                        //TODO PickupLocationID
      }else
        curRts(NRouts).setStopNum(-1)
      N += 1
      S+=1
    }
    //TODO every vehicle has vehicleID and Initial Time as current Time
    //todo The last route!!#################################
    curRts(NRouts).setStops(stops)                                       //TODO attached stops with planed service and stopID
    if (curRts(NRouts).getStopNum > -1) curRts(NRouts).setStopNum(S)     //TODO the Number of stops
    val tmp=vehicleLoc.split(",")
    curRts(NRouts).setLatStart(tmp(0).toDouble)                           //TODO starting LocationTime
    curRts(NRouts).setLngStart(tmp(1).toDouble)
    //todo The last route!!#################################

    //    val startOfDay= Math.floor(allStops(0).getOpenCloseTime(0) / (24 * 60 * 60 * 1000)).toLong * 24 * 60 * 60 * 1000
    //startOfDay+6*60*60*1000+0*60*1000+0*1000,
    val results2 = MainPreSchd.mainRealTimeAdj(Array(curRts(NRouts)),allStops)


  }

  private def calHistServTime(data: List[List[String]]):  Map[String,Long] = {


    var LocID_Key: List[String] = Nil
    var LocID_SrvTim= Array.ofDim[Long](99999,2000)
    var LocID_count= Array.ofDim[Int](99999)
    var res=Map.empty[String,Long]

    for(i<-2 to data.size-1){
      if(data(i)(7).length>2) {
        val ind3=LocID_Key.indexOf(data(i)(28).toString)
        if (ind3 > -1) {
          LocID_count(ind3)+=1
          LocID_SrvTim(ind3)(LocID_count(ind3)) =Math.floor(data(i)(7).toString.toDouble * 60 * 60 * 1000).toLong
        }
        else {
          LocID_Key = LocID_Key ::: List(data(i)(28).toString)
          val ind=LocID_Key.size-1
          LocID_SrvTim(ind)(0) =Math.floor(data(i)(7).toString.toDouble * 60 * 60 * 1000).toLong
        }
      }
    }
    var LocID_AverageSrvTime: List[Long] = Nil
    for(i<-0 to LocID_Key.size-1){
      var sum:Long=0
      for(j<-0 to LocID_count(i))
        sum+=LocID_SrvTim(i)(j)
      LocID_AverageSrvTime=LocID_AverageSrvTime:::List(Math.floor(sum/(LocID_count(i)+1)).toLong)
      res+=(LocID_Key(i)-> Math.floor(sum/(LocID_count(i)+1)).toLong)
    }

    return res

  }


  def testingABIData():List[RoutsFinalS]= {

    var results=new ListBuffer[RoutsFinalS]

    var curRts = Array.ofDim[RoutsFinalS](99)
    var stops = Array.ofDim[StopsS](99)
    var allStops = Array.ofDim[StopsS](999)
    val par:Paramet=new Paramet
    var vehicleLoc:String="32.7092285,-117.1682281"
    val reader = com.github.tototoshi.csv.CSVReader.open(new File(schConf.workingDrectory+"ABI_inputData.csv"))
    val data=reader.all(); reader.close()

    /////////////////////////Reading from CSV file///////////////
    var N: Int = 0
    var NRouts: Int = 0

    for(i<-2 to data.size-1){
      if (!curRts(NRouts).isInstanceOf[RoutsFinalS]) {
        curRts(NRouts) = new RoutsFinalS
        curRts(NRouts).setVehicleID(data(i)(6).toString)//TODO Route ID
        curRts(NRouts).setDriverName(data(i)(5).toString)//TODO Route ID
        curRts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString)) //todo initial time
      } else if(!curRts(NRouts).getVehicleID.contains(data(i)(6).toString)) {

        curRts(NRouts).setStops(stops)
        val tmp = vehicleLoc.split(",")
        curRts(NRouts).setLatStart(tmp(0).toDouble)   //TODO every vehicle has vehicleID and Initial Time as current Time, vehicle location
        curRts(NRouts).setLngStart(tmp(1).toDouble)

        if (curRts(NRouts).getStopNum > -1) curRts(NRouts).setStopNum(N)

        ///////////////////////////////////////////////////////////////////////////////////////
        //        val warningResults=MainPreSchd.mainRealTimeWarningMethod(Array(curRts(NRouts)), allStops)
        //         println(FunctionsVRPs.tage(warningResults,false))
        val results2 = MainPreSchd.mainCalculateETAforABI_Data(Array(curRts(NRouts)), allStops)
        results += (results2(0))


        ////////////////////////////////////////////////////////////////////////////////

        stops = Array.ofDim[StopsS](99)
        allStops = Array.ofDim[StopsS](99)
        N = 0
        NRouts += 1
        curRts(NRouts) = new RoutsFinalS
        curRts(NRouts).setVehicleID(data(i)(6).toString)//TODO Route ID
        curRts(NRouts).setInitialTime(par.formatDate.parse(data(i)(2).toString)) //todo initial time
      }

      stops(N)= new StopsS
      allStops(N)= new StopsS

      allStops(N).setTaskId(N.toString+"RealID") //todo this is the Task ID
      stops(N).setTaskId(N.toString)  //TODO This should be the order

      data(i)(4).toString.trim match {//TODO Priority
        case "B" => allStops(N).setPriority(1)
        case "U" => allStops(N).setPriority(2)
        case "D" => allStops(N).setPriority(3)
      }

      if(data(i)(14).length>2) {//TODO OpenAndCloseTime
        allStops(N).setOpenCloseTime(Array(par.formatDate.parse(data(i)(14).toString).getTime, par.formatDate.parse(data(i)(15).toString).getTime))
      }else
      {println("Warning, no open and close time imported.")
        allStops(N).setOpenCloseTime(Array(par.formatDate.parse(data(i)(2).toString).getTime, par.formatDate.parse(data(i)(2).toString).getTime+ 24 * 60 * 60 * 1000))
      }

      /*
            if(data(i)(12).length>2) { //TODO ActualArrivalTime
              allStops(N).setActualArrivalTime(par.formatDate.parse(data(i)(12).toString).getTime)
            }
      */

      var open:List[Long]=Nil
      var close:List[Long]=Nil
      if(data(i)(16).length>2){ //TODO TimeWindow 1
        open=open:::List(par.formatDate.parse(data(i)(16).toString).getTime)
        close=close:::List(par.formatDate.parse(data(i)(17).toString).getTime)
      }else{
        open=open:::List(allStops(N).getOpenCloseTime(0))
        close=close:::List(allStops(N).getOpenCloseTime(1))
      }

      if(data(i)(18).length>2) {//TODO TimeWindow 2
        open = open ::: List(par.formatDate.parse(data(i)(18).toString).getTime)
        close = close ::: List(par.formatDate.parse(data(i)(19).toString).getTime)
      }

      if(data(i)(8).length>1) {
        allStops(N).setServiceTime(Math.floor(data(i)(8).toString.toDouble * 60 * 60 * 1000).toLong) //TODO Actual ServiceTime
        stops(N).setServiceTime(Math.floor(data(i)(8).toString.toDouble * 60 * 60 * 1000).toLong) //TODO Planned ServiceTime
      }else {
        allStops(N).setServiceTime(schConf.defaultServiceTime) //TODO Actual ServiceTime
        stops(N).setServiceTime(schConf.defaultServiceTime)        //TODO Planned ServiceTime
      }

      allStops(N).setOpenTWindow(open.toArray);open=List()
      allStops(N).setCloseTWindow(close.toArray); close=List()

      if(!data(i)(29).contains("N/A")) {
        allStops(N).setPickupAddress(data(i)(29)) //TODO PickupAddress
        allStops(N).setPickupCity(data(i)(30))

        allStops(N).setLocationID(data(i)(28))                        //TODO PickupLocationID
        HereAPIs.callGeocod(allStops(N).getPickupAddress.replaceAll("[\\s+()]", "+"), schConf.workingDrectory)
        val outputS: String = HereAPIs.readGeocoding(null, schConf.workingDrectory)
        val token: Array[String] = outputS.split(",")
        if (token(0).length > 2) {
          allStops(N).setLatPickup(token(0).toDouble)
          allStops(N).setLngPickup(token(1).toDouble)
          allStops(N).setLatDelivery(token(0).toDouble)
          allStops(N).setLngDelivery(token(1).toDouble)
        }else{
          println("Invalid address for location id: "+data(i)(28)+" address: " + data(i)(29) + " " + data(i)(30))
          N -=1
        }//remove the current stop

      }else{
        println("Invalid address for location id: "+data(i)(28)+" address: " + data(i)(29) + " " + data(i)(30))
        N -=1}//remove the current stop
      N += 1
    }
    //TODO every vehicle has vehicleID and Initial Time as current Time
    //todo The last route!!#################################
    curRts(NRouts).setStops(stops)                                       //TODO attached stops with planed service and stopID
    if (curRts(NRouts).getStopNum > -1) curRts(NRouts).setStopNum(N)     //TODO the Number of stops
    val tmp=vehicleLoc.split(",")
    curRts(NRouts).setLatStart(tmp(0).toDouble)                           //TODO starting LocationTime
    curRts(NRouts).setLngStart(tmp(1).toDouble)
    //todo The last route!!#################################


    val results2 = MainPreSchd.mainCalculateETAforABI_Data(Array(curRts(NRouts)),allStops)
    results += (results2(0))

    return results.toList


  }




}
