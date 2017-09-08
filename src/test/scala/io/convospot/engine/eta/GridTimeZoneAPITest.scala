package io.convospot.engine.eta
import io.convospot.cep.events._

/**
 * Created by ali on 8/28/15.
 */
object GridTimeZoneAPITest extends App{
  //  def is = sequential ^ s2"""
  //        filter top n closest vehicles $testingTimeZone
  //    """

  override def main (args: Array[String]): Unit = {

    val loc = Location(41.369994, -70.103745)
//    val loc = Location(41.429700, -91.077788)

    val results = GridTimeZoneAPI.getTimeZone(loc,System.currentTimeMillis()/1000)
    println((results get "OK").get)


  }
}

