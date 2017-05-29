// NextRide.scala
package com.peopledesigned.askrtd.nextride.model

import scala.beans.BeanProperty
import com.fasterxml.jackson.annotation.JsonProperty

case class NextRideLocationAttributes(
  @BeanProperty var stopName:String,
  @BeanProperty var stopLat: Double,
  @BeanProperty var stopLon: Double,
  @BeanProperty var locationType: Integer,
  @BeanProperty var parentStation: NextRideLocationStops,
  @BeanProperty var location: Array[Double],
  @BeanProperty var routesServed: Array[String],
  @BeanProperty var modesServed: Array[String],
  @BeanProperty var distanceFromLocation: Integer
) {
  def this() = this("", 0.0, 0.0, 0, null, Array[Double](), Array[String](), Array[String](), 0)
}

case class NextRideLocationStops(
  // @JsonProperty("type") TODO
  @BeanProperty var stopType:String,
  @BeanProperty var id: String,
  @BeanProperty var attributes: NextRideLocationAttributes
) {
  def this() = this(stopType="", "", new NextRideLocationAttributes)
}

case class NextRideResponse(
  @BeanProperty var data:Array[NextRideLocationStops]
) {
  def this() = this(Array[NextRideLocationStops](new NextRideLocationStops()))
}
    // {
    //   "type": "nextride-location-stops",
    //   "id": "14087",
    //   "attributes": {
    //     "stopName": "Gillaspie Dr & Greenbriar Blvd",
    //     "stopLat": 39.970452,
    //     "stopLon": -105.247377,
    //     "locationType": 0,
    //     "parentStation": null,
    //     "location": [
    //       39.970452,
    //       -105.247377
    //     ],
    //     "routesServed": [
    //       "206F"
    //     ],
    //     "modesServed": [
    //       "Bus"
    //     ],
    //     "distanceFromLocation": 2400
    //   }
    // },