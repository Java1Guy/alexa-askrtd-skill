package com.peopledesigned.askrtd.util

import java.util

import com.amazon.speech.slu.Intent
import com.amazon.speech.speechlet.interfaces.system.{SystemInterface, SystemState}
import com.amazon.speech.speechlet.{Context, SpeechletResponse}
import com.amazon.speech.ui._
import com.peopledesigned.askrtd.model.Address

/**
  * Created by mchance on 5/28/17.
  */
object ResponseHelper {
  /**
    * This is the default title that this skill will be using for cards.
    */
  val ADDRESS_CARD_TITLE = "Ask R.T.D. Skill"

  val WELCOME_TEXT = "Welcome to ask r.t.d. Skill! What do you want to ask?"
  val HELP_TEXT = "You can use this skill by asking something like: whats the nearest stop"
  val UNHANDLED_TEXT = "This is unsupported. Please ask something else."
  val ERROR_TEXT = "There was an error with the skill. Please try again."

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

  /**
    * Creates a {@code SpeechletResponse} for the GetAddress intent.
    *
    * @return SpeechletResponse spoken and visual response for the given intent
    */
  def getAddressResponse(address: Address) = {
    val speechText = "Your address is " + address.addressLine1.get + " " + address.stateOrRegion + ", " + address.postalCode.get
    val card = getSimpleCard(ADDRESS_CARD_TITLE, speechText)
    val speech = getPlainTextOutputSpeech(speechText)
    SpeechletResponse.newTellResponse(speech, card)
  }

  /**
    * Creates a {@code SpeechletResponse} for permission requests.
    *
    * @return SpeechletResponse spoken and visual response for the given intent
    */
  def getPermissionsResponse = {
    val speechText = "You have not given this skill permissions to access your address. " + "Please give this skill permissions to access your address."
    // Create the permission card content.
    // The differences between a permissions card and a simple card is that the
    // permissions card includes additional indicators for a user to enable permissions if needed.
    val card = new AskForPermissionsConsentCard
    card.setTitle(ADDRESS_CARD_TITLE)
    val permissions = new util.HashSet[String]
    permissions.add(ALL_ADDRESS_PERMISSION)
    card.setPermissions(permissions)
    val speech = getPlainTextOutputSpeech(speechText)
    SpeechletResponse.newTellResponse(speech, card)
  }

  /**
    * Helper method that retrieves the system state from the request context.
    *
    * @param context request context.
    * @return SystemState the systemState
    */
  def getSystemState(context: Context) = context.getState(classOf[SystemInterface], classOf[SystemState])

  /**
    * Helper method that creates a card object.
    *
    * @param title   title of the card
    * @param content body of the card
    * @return SimpleCard the display card to be sent along with the voice response.
    */
  def getSimpleCard(title: String, content: String) = {
    val card = new SimpleCard
    card.setTitle(title)
    card.setContent(content)
    card
  }

  /**
    * Helper method that will get the intent name from a provided Intent object. If a name does not
    * exist then this method will return null.
    *
    * @param intent intent object provided from a skill request.
    * @return intent name or null.
    */
  def getIntentName(intent: Intent) = if (intent != null) intent.getName
  else null

  /**
    * Helper method for retrieving an OutputSpeech object when given a string of TTS.
    *
    * @param speechText the text that should be spoken out to the user.
    * @return an instance of SpeechOutput.
    */
  def getPlainTextOutputSpeech(speechText: String) = {
    val speech = new PlainTextOutputSpeech
    speech.setText(speechText)
    speech
  }

  /**
    * Helper method that returns a reprompt object. This is used in Ask responses where you want
    * the user to be able to respond to your speech.
    *
    * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
    * @return Reprompt instance.
    */
  def getReprompt(outputSpeech: OutputSpeech) = {
    val reprompt = new Reprompt
    reprompt.setOutputSpeech(outputSpeech)
    reprompt
  }

  /**
    * Helper method for retrieving an Ask response with a simple card and reprompt included.
    *
    * @param cardTitle  Title of the card that you want displayed.
    * @param speechText speech text that will be spoken to the user.
    * @return the resulting card and speech text.
    */
  def getAskResponse(cardTitle: String, speechText: String) = {
    val card = getSimpleCard(cardTitle, speechText)
    val speech = getPlainTextOutputSpeech(speechText)
    val reprompt = getReprompt(speech)
    SpeechletResponse.newAskResponse(speech, reprompt, card)
  }
}
