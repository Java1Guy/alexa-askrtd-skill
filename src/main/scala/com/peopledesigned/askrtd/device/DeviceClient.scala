package com.peopledesigned.askrtd.device

import java.io.IOException

import com.fasterxml.jackson.databind.ObjectMapper
import com.peopledesigned.askrtd.model.{Address, DeviceAddressClientException, UnauthorizedException}
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by mchance on 5/28/17.
  */

/**
  * This is a small wrapper client around the Alexa Device Address API.
  */
object DeviceClient {
  val log: Logger = LoggerFactory.getLogger(classOf[DeviceClient])
  val BASE_API_PATH = "/v1/devices/"
  val SETTINGS_PATH = "/settings/"
  val FULL_ADDRESS_PATH = "address"
  val COUNTRY_AND_POSTAL_CODE_PATH = "address/countryAndPostalCode"
//  https://api.amazonalexa.com/
}

/**
  * Constructor for the DeviceClient. It will take the device ID, consent token, and api endpoint.
  * Those values will be used when making requests to the Device Address API.
  *
  * @param deviceId     the deviceId of the device being retrieved.
  * @param consentToken the consentToken used for authorization against the Device Address API.
  * @param apiEndpoint  the endpoint of the Device Address API. This could be the address endpoint for NA, EU, etc.
  */
class DeviceClient(var deviceId: String, var consentToken: String, var apiEndpoint: String) {

  /**
    * This method will make a request to the Device Address API path for retrieving the full address.
    *
    * @return JsonNode the JSON response from the API.
    * @throws DeviceAddressClientException When the client fails to perform or complete the request
    */
  @throws[DeviceAddressClientException]
  def getFullAddress: Address = {
    val closeableHttpClient = HttpClients.createDefault
    val requestUrl = apiEndpoint + DeviceClient.BASE_API_PATH + deviceId + DeviceClient.SETTINGS_PATH + DeviceClient.FULL_ADDRESS_PATH
    DeviceClient.log.info("Request will be made to the following URL: {}", requestUrl)
    val httpGet = new HttpGet(requestUrl)
    httpGet.addHeader("Authorization", "Bearer " + consentToken)
    DeviceClient.log.info("Sending request to Device Address API")
    try {
      val addressResponse = closeableHttpClient.execute(httpGet)
      val statusCode = addressResponse.getStatusLine.getStatusCode
      DeviceClient.log.info("The Device Address API responded with a status code of {}", statusCode)
      if (statusCode == HttpStatus.SC_OK) {
        val httpEntity = addressResponse.getEntity
        val responseBody = EntityUtils.toString(httpEntity)
        val objectMapper = new ObjectMapper
        val address = objectMapper.readValue(responseBody, classOf[Address])
        return address
      }
      else if (statusCode == HttpStatus.SC_FORBIDDEN) {
        DeviceClient.log.info("Failed to authorize with a status code of {}", statusCode)
        throw new UnauthorizedException("Failed to authorize.")
      }
      else {
        val errorMessage = "Device Address API query failed with status code of " + statusCode
        DeviceClient.log.info(errorMessage)
        throw new DeviceAddressClientException(errorMessage)
      }
    } catch {
      case e: IOException =>
        throw new DeviceAddressClientException(e.getMessage)
    } finally DeviceClient.log.info("Request to Address Device API completed.")
  }
}
