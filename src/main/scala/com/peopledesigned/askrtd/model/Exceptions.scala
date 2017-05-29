package com.peopledesigned.askrtd.model

/**
 * Created by mchance on 5/28/17.
 **/
case class DeviceAddressClientException(message: String) extends Exception(message)

case class UnauthorizedException(message: String) extends Exception(message)
