package com.peopledesigned.askrtd.model

/**
  * Created by mchance on 5/28/17.
  */
case class Address (
  stateOrRegion: Option[String],
  city: Option[String],
  countryCode: Option[String],
  postalCode: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  addressLine3: Option[String],
  districtOrCounty: Option[String]
)
{
  def this() = this(None,None,None,None,None,None,None,None)
}