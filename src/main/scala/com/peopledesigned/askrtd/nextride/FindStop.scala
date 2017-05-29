// FindStop.scala
package com.peopledesigned.askrtd.nextride
import com.fasterxml.jackson.databind.ObjectMapper
import com.peopledesigned.askrtd.nextride.model.NextRideLocationStops

object FindStop {
  var stopsSearchRadius = 1000

  def findStops(lat: Double, lon: Double): Array[NextRideLocationStops] = {
    val url = s"https://www.rtd-denver.com/api/nextride/location/stops?lat=$lat&lon=$lon&radius=$stopsSearchRadius"
    val stopJson = com.peopledesigned.askrtd.util.UrlReader.get(url)
    // System.out.println(stopJson)
    val sjb = stopJson.replaceAll("\"type\"", "\"stopType\"").getBytes
    val objectMapper = new ObjectMapper()
    // objectMapper.registerModule(DefaultScalaModule)
    //convert json string to object
    val orderObj = objectMapper.readValue(sjb, classOf[com.peopledesigned.askrtd.nextride.model.NextRideResponse])
    orderObj.data
  }

  def findClosestStop(lat: Double, lon: Double): NextRideLocationStops = {
    val stops = findStops(lat, lon)
    stops.reduceLeft((x,y) => if (x.attributes.distanceFromLocation < y.attributes.distanceFromLocation) x else y)
  }
}