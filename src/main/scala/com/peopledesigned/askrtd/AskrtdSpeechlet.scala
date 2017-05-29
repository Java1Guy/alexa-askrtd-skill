package com.peopledesigned.askrtd

import com.amazon.speech.json.SpeechletRequestEnvelope
import com.amazon.speech.speechlet._
import com.peopledesigned.askrtd.device.DeviceClient
import com.peopledesigned.askrtd.model.{DeviceAddressClientException, UnauthorizedException}
import com.peopledesigned.askrtd.util.ResponseHelper._
import org.slf4j.LoggerFactory

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
//    A LaunchRequest always starts a new session.
//    val response: SpeechletResponse = new SpeechletResponse
    // Get device address -- save in session? or device ID?

    val consentToken = requestEnvelope.getSession.getUser.getPermissions.getConsentToken
    if (consentToken == null) {
      log.info("The user hasn't authorized the skill. Sending a permissions card.")
      return getPermissionsResponse
    }
    try {
      val systemState = getSystemState(requestEnvelope.getContext)
      val deviceId = systemState.getDevice.getDeviceId
      val apiEndpoint = systemState.getApiEndpoint
      val alexaDeviceAddressClient = new DeviceClient(deviceId, consentToken, apiEndpoint)
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
    val intentName = getIntentName(intent)

    log.info("Intent received: {}", intentName)

    // We want to handle each intent differently here, so that we can give each a unique response.
    // Refer to the Interaction Model for more information:
    // https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/alexa-skills-kit-interaction-model-reference
    intentName match { // This is the custom intent that delivers the main functionality of the sample skill.
      // Refer to speechAssets/SampleUtterances for examples that would trigger this.
      case "GetAddress" =>
        val consentToken = session.getUser.getPermissions.getConsentToken
        if (consentToken == null) {
          log.info("The user hasn't authorized the skill. Sending a permissions card.")
          return getPermissionsResponse
        }
        try {
          val systemState = getSystemState(requestEnvelope.getContext)
          val deviceId = systemState.getDevice.getDeviceId
          val apiEndpoint = systemState.getApiEndpoint
          val alexaDeviceAddressClient = new DeviceClient(deviceId, consentToken, apiEndpoint)
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
      // This is one of the many Amazon built in intents.
      // Refer to the following for a list of all available built in intents:
      // https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/built-in-intent-ref/standard-intents
      case "AMAZON.HelpIntent" =>
        getAskResponse(ADDRESS_CARD_TITLE, HELP_TEXT)
      case _ =>
        getAskResponse(ADDRESS_CARD_TITLE, UNHANDLED_TEXT)
    }
  }
}