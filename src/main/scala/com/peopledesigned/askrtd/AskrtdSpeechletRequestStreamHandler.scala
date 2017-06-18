package com.peopledesigned.askrtd

import scala.collection.JavaConverters._
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler
import com.peopledesigned.askrtd.util.Properties

/**
  * Created by mchance on 5/29/17.
  */
object AskrtdSpeechletRequestStreamHandler {
  /*
   * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
   * Alexa Skill and put the relevant Application Ids in this Set.
   */
  val supportedApplicationIds = Set(Properties.getString(Properties.ALEXA_API_KEY))
}

class AskrtdSpeechletRequestStreamHandler
  extends SpeechletRequestStreamHandler(
    new AskrtdSpeechlet,
    AskrtdSpeechletRequestStreamHandler.supportedApplicationIds.asJava
  ) {

}
