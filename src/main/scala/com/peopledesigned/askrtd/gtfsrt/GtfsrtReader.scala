// GtfsrtReader.scala
package com.peopledesigned.askrtd.gtfsrt

import java.io.ByteArrayInputStream
import scala.collection.JavaConversions._

object GtfsrtReader {
  val rtd_sync = "http://www.rtd-denver.com/google_sync/"
  def getTripUpdate() = {
    val tripUpdatePb = com.peopledesigned.askrtd.util.UrlReader.getAuthenticatedStream(rtd_sync+"TripUpdate.pb")
    val feedMsg = com.google.transit.realtime.GtfsRealtime.FeedMessage.parseFrom(tripUpdatePb)
    val el = feedMsg.getEntityList

    // val fe:com.google.transit.realtime.GtfsRealtime.FeedEntity = feedMsg.getEntityList.get(0)
    // assert(feedMsg != null)
    for (ent <- el) {
      if (ent.hasId()) println("Ent id: "+ent.getId())
      if (ent.hasTripUpdate()) {
        val tu = ent.getTripUpdate
        println("Trip Update: "+tu)
        // tu.getStopTimeUpdateList -> java.util.List[com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate]
        if (tu.hasTrip()) {
          val trip = tu.getTrip // TripDescriptor
          if (trip.hasRouteId()) println("Trip route id: "+trip.getRouteId)
          // println("TU trip: "+tu.getTrip())
        }
        val stuList = tu.getStopTimeUpdateList
        for (stu <- stuList) {
          if (stu.hasStopId()) println(stu.getStopId)
          if (stu.hasDeparture()) {
            println(stu.getDeparture)
            if (stu.getDeparture.hasTime) println (stu.getDeparture.getTime)
          }
          if (stu.hasArrival()) {
            println(stu.getArrival)
            if (stu.getArrival.hasTime) println (stu.getArrival.getTime)
          }
        }
      }
      if (ent.hasVehicle()) println("Ent vehicle: "+ent.getVehicle())
      if (ent.hasAlert()) println("Ent alert: "+ent.getAlert())
    }

  }
  def getVehiclePostion() = {
    val vehiclePositionPb = com.peopledesigned.askrtd.util.UrlReader.getAuthenticatedStream(rtd_sync+"VehiclePosition.pb")
  }
}