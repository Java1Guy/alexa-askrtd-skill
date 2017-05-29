package com.peopledesigned.askrtd.util

import scala.io.Source

/**
  * Created by mchance on 5/29/17.
  */
object Properties {
  var properties : java.util.Properties = null
  var GOOGLE_API_KEY: String = "google.geoencoding.apikey"
  var ALEXA_API_KEY: String = "alexa.application.apikey"

  val url = getClass.getResource("/keys.properties")
  if (url != null) {
    val source = Source.fromURL(url)

    properties = new java.util.Properties()
    properties.load(source.bufferedReader())
  }

  def getString(key: String): String = {
    properties.getProperty(key)
  }
}
