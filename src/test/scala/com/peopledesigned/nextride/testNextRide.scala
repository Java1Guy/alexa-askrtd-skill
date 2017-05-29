// testNextRide.scala
package com.peopledesigned.nextride

import org.scalatest.FlatSpec
/**
  * Created by mchance on 3/12/17.
  */

class NextRideSpec extends FlatSpec {

  "NextRide" should "find stops" in {
    val results = com.peopledesigned.askrtd.nextride.FindStop.findStops(39.974575, -105.241433)
    assert(results != null)
    assert(results.length > 0)
    assert(results(0).attributes != null)
    System.out.println("Routes served: "+results(0).attributes.routesServed(0))
  }

  "NextRide" should "find the closest stop" in {
    val result = com.peopledesigned.askrtd.nextride.FindStop.findClosestStop(39.974575, -105.241433)
    assert(result != null)
    System.out.println("Closest stop: "+result.id+", attr: "+result.attributes)
  }
}