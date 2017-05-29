package com.peopledesigned.askrtd.geocode

import com.google.maps._
import com.google.maps.model.GeocodingResult
import com.peopledesigned.askrtd.util.Properties
/**
  * Created by mchance on 3/12/17.
  */

object GeoEncoder {

  def findAddress(address:String): Option[com.google.maps.model.LatLng] = {
    var context:GeoApiContext = new GeoApiContext().setApiKey(
      Properties.getString(Properties.GOOGLE_API_KEY))
    var results:Array[GeocodingResult] =  GeocodingApi.geocode(context,
        address).await()
    if (results == null || results.size == 0) return None
    Some(results(0).geometry.location)
  }
}