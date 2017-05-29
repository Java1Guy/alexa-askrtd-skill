// UrlReader.scala
package com.peopledesigned.askrtd.util
import java.util.Base64

object UrlReader {
  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def get(url: String,
      connectTimeout: Int = 5000,
      readTimeout: Int = 5000,
      requestMethod: String = "GET") = getWithAuthentication(url, connectTimeout, readTimeout, requestMethod)

  def getAuthenticated(url: String,
      connectTimeout: Int = 5000,
      readTimeout: Int = 5000,
      requestMethod: String = "GET",
      authStr: Option[String] = None) = getWithAuthentication(url, connectTimeout, readTimeout, requestMethod, authStr)

  def getAuthenticatedStream(url: String,
      connectTimeout: Int = 5000,
      readTimeout: Int = 5000,
      requestMethod: String = "GET") = getWithAuthenticationStream(url, connectTimeout, readTimeout, requestMethod, Some("RTDgtfsRT:realT!m3Feed"))

  def getWithAuthentication(url: String,
      connectTimeout: Int = 5000,
      readTimeout: Int = 5000,
      requestMethod: String = "GET",
      authStr: Option[String] = None) =
  {
    val inputStream = getWithAuthenticationStream(url, connectTimeout, readTimeout, requestMethod, authStr)
    val content = io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close
    content
  }

  def getWithAuthenticationStream(url: String,
      connectTimeout: Int = 5000,
      readTimeout: Int = 5000,
      requestMethod: String = "GET",
      authStr: Option[String] = None) =
  {
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    if (!authStr.isEmpty) {
      val bytesEncoded = Base64.getEncoder().encodeToString(authStr.get.getBytes());
      connection.setRequestProperty("Authorization", "Basic "+bytesEncoded);
    }
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    connection.getInputStream
  }
}