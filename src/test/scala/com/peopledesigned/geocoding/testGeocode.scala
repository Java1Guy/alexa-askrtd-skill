// testGeocode.scala
package com.peopledesigned.geocoding

import org.scalatest.FlatSpec
// import com.google.transit.realtime
import java.io.FileInputStream
import java.io.IOException
import com.google.maps._
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LocationType
/**
  * Created by mchance on 3/12/17.
  */

class GeoCodeSpec extends FlatSpec {

  "Geocoded address" should "have lat lon" in {
    val location = com.peopledesigned.askrtd.geocode.GeoEncoder.findAddress("1600 Amphitheatre Parkway Mountain View, CA 94043")
    assert(!location.isEmpty)
    assert(37.4224082 == location.get.lat)
    assert(-122.0856086 == location.get.lng)
    // assert(LocationType.ROOFTOP == results(0).geometry.locationType)
  }
}