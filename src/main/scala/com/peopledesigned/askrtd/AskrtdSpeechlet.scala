package com.peopledesigned.askrtd

import com.amazon.speech.json.SpeechletRequestEnvelope
import com.amazon.speech.speechlet._
import com.peopledesigned.askrtd.device.DeviceClient
import com.peopledesigned.askrtd.model.{DeviceAddressClientException, UnauthorizedException}
import com.peopledesigned.askrtd.util.ResponseHelper.{getAskResponse, _}
import org.slf4j.LoggerFactory
import com.peopledesigned.askrtd.intent.ProcessIntent

class AskrtdSpeechlet extends SpeechletV2 {
  private val log = LoggerFactory.getLogger(classOf[AskrtdSpeechlet])

  /**
    * The permissions that this skill relies on for retrieving addresses. If the consent token isn't
    * available or invalid, we will request the user to grant us the following permission
    * via a permission card.
    *
    * Another Possible value if you only want permissions for the country and postal code is:
    * read::alexa:device:all:address:country_and_postal_code
    * Be sure to check your permissions settings for your skill on https://developer.amazon.com/
    */
  val ALL_ADDRESS_PERMISSION = "read::alexa:device:all:address"

  override def onSessionEnded(requestEnvelope: SpeechletRequestEnvelope[SessionEndedRequest]): Unit = {
    val requestId = requestEnvelope.getRequest.getRequestId
    val sessionId = requestEnvelope.getSession.getSessionId
    log.info(s"onSessionEnded $requestId, $sessionId")
  }

//  Not clear this is called
  override def onSessionStarted(requestEnvelope: SpeechletRequestEnvelope[SessionStartedRequest]): Unit = {
    val requestId = requestEnvelope.getRequest.getRequestId
    val sessionId = requestEnvelope.getSession.getSessionId
    log.info(s"onSessionStarted $requestId, $sessionId")
  }

  override def onLaunch(requestEnvelope: SpeechletRequestEnvelope[LaunchRequest]): SpeechletResponse = {
    log.info(">>> onLaunch")
//    A LaunchRequest always starts a new session.
//    val response: SpeechletResponse = new SpeechletResponse
    // Get device address -- save in session? or device ID?

    try {
      val systemState = getSystemState(requestEnvelope.getContext)
      val deviceId = systemState.getDevice.getDeviceId
      val apiEndpoint = systemState.getApiEndpoint
      val (consentToken, response) = getLaunchConsentOrResponse(requestEnvelope)
      if (consentToken.isEmpty) return response.get
      val alexaDeviceAddressClient = new DeviceClient(deviceId, consentToken.get, apiEndpoint)
      val addressObject = alexaDeviceAddressClient.getFullAddress
      if (addressObject == null) return getAskResponse(ADDRESS_CARD_TITLE, ERROR_TEXT)
      return getAddressResponse(addressObject)

    } catch {
      case e: UnauthorizedException =>
        return getPermissionsResponse
      case e: DeviceAddressClientException =>
        log.error("Device Address Client failed to successfully return the address.", e)
        return getAskResponse(ADDRESS_CARD_TITLE, ERROR_TEXT)
    }
    getAskResponse(ADDRESS_CARD_TITLE, WELCOME_TEXT)
  }

  override def onIntent(requestEnvelope: SpeechletRequestEnvelope[IntentRequest]): SpeechletResponse = {
    val intentRequest = requestEnvelope.getRequest
    val requestId = intentRequest.getRequestId
    val session = requestEnvelope.getSession
    val sessionId = session.getSessionId
    log.info(s"onIntent $requestId, $sessionId")
    val intent = intentRequest.getIntent
    ProcessIntent.handleIntent(intent)
  }

  def getLaunchConsentOrResponse(requestEnvelope: SpeechletRequestEnvelope[LaunchRequest]): (Option[String], Option[SpeechletResponse]) = {
    val session = requestEnvelope.getSession
    if (session == null) {
      log.info("There is no session.")
      return (None, Some(getAskResponse(ADDRESS_CARD_TITLE, WELCOME_TEXT)))
    }
    log.info("Have session, getting user.")
    getConsentOrResponse(session)
  }

  def getIntentConsentOrResponse(requestEnvelope: SpeechletRequestEnvelope[IntentRequest]): (Option[String], Option[SpeechletResponse]) = {
    val session = requestEnvelope.getSession
    if (session == null) {
      log.info("There is no session.")
      return (None, Some(getAskResponse(ADDRESS_CARD_TITLE, WELCOME_TEXT)))
    }
    log.info("Have session, getting user.")
    getConsentOrResponse(session)
  }
  def getConsentOrResponse(session: Session): (Option[String], Option[SpeechletResponse]) = {
    val user = session.getUser
    if (user == null) {
      log.info("There is no user.")
      return (None, Some(getAskResponse(ADDRESS_CARD_TITLE, WELCOME_TEXT)))
    }
    log.info("Have user, getting permissions.")
    val permissions = user.getPermissions
    if (permissions == null) {
      log.info("The user hasn't authorized the skill. Sending a permissions card.")
      return (None, Some(getPermissionsResponse))
    }
    log.info("Have permissions, getting consent.")
    val consentToken = permissions.getConsentToken
    if (consentToken == null) {
      log.info("The user hasn't authorized the skill. Sending a permissions card.")
      return (None, Some(getPermissionsResponse))
    }
    (Some(consentToken), None)
  }
  def getAddress(session: Session, requestEnvelope: SpeechletRequestEnvelope[IntentRequest]): SpeechletResponse = {
    val consentToken = getConsentOrResponse(session)
    if (consentToken._1.isEmpty) {
      log.info("The user hasn't authorized the skill. Sending a permissions card.")
      return consentToken._2.getOrElse(getPermissionsResponse)
    }
    try {
      val systemState = getSystemState(requestEnvelope.getContext)
      val deviceId = systemState.getDevice.getDeviceId
      val apiEndpoint = systemState.getApiEndpoint
      val alexaDeviceAddressClient = new DeviceClient(deviceId, consentToken._1.get, apiEndpoint)
      val addressObject = alexaDeviceAddressClient.getFullAddress
      if (addressObject == null) return getAskResponse(ADDRESS_CARD_TITLE, ERROR_TEXT)
      getAddressResponse(addressObject)
    } catch {
      case e: UnauthorizedException =>
        getPermissionsResponse
      case e: DeviceAddressClientException =>
        log.error("Device Address Client failed to successfully return the address.", e)
        getAskResponse(ADDRESS_CARD_TITLE, ERROR_TEXT)
    }
  }
}