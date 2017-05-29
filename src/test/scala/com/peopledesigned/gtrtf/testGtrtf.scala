package com.peopledesigned.gtrtf

import org.scalatest.FlatSpec
// import com.google.transit.realtime
import java.io.FileInputStream
import java.io.IOException
import scala.collection.JavaConversions._
/**
  * Created by mchance on 3/12/17.
  */

class GtrtfSpec extends FlatSpec {

  "Trip Update buffer" should "have messages" in {
    val fis = new FileInputStream("src/test/resources/TripUpdate.pb")
    assert(fis != null)
    com.peopledesigned.askrtd.gtfsrt.GtfsrtReader.getTripUpdate
    // val vp: VehiclePosition = fe.vehicle
  }

  "Vehicle position buffer" should "have messages" in {
    val fis = new FileInputStream("src/test/resources/VehiclePosition.pb")
    assert(fis != null)
    com.peopledesigned.askrtd.gtfsrt.GtfsrtReader.getVehiclePostion
    // val vp: VehiclePosition = fe.vehicle
  }
}