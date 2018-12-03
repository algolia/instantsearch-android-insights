package com.algolia.instantsearch.insights.webservice

import com.algolia.instantsearch.insights.converter.ConverterEventToString
import com.algolia.instantsearch.insights.event.Event
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


internal class WebServiceHttp(
    private val appId: String,
    private val apiKey: String,
    private val environment: Environment,
    private val connectTimeoutInMilliseconds: Int,
    private val readTimeoutInMilliseconds: Int
) : WebService {


    enum class Environment(baseUrl: String) {
        Prod("https://insights.algolia.io"),
        Debug("http://localhost:8080");

        val url: String = "$baseUrl/1/events"
    }

    override fun send(vararg event: Event): WebService.Response {
        val array = JSONArray(ConverterEventToString.convert(event.toList()).map(::JSONObject))
        val string = JSONObject().put("events", array).toString()
        val url = URL(environment.url)
        val connection = (url.openConnection() as HttpURLConnection).also {
            it.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            it.setRequestProperty("Accept", "application/json")
            it.setRequestProperty("X-Algolia-Application-Id", appId)
            it.setRequestProperty("X-Algolia-API-Key", apiKey)
            it.setRequestProperty("Content-Length", string.length.toString())
            it.requestMethod = "POST"
            it.connectTimeout = connectTimeoutInMilliseconds
            it.readTimeout = readTimeoutInMilliseconds
            it.doOutput = true
            it.useCaches = false
        }
        connection.outputStream.write(string.toByteArray())
        val responseCode = connection.responseCode
        val errorMessage = connection.errorStream?.bufferedReader()?.readText()
        connection.disconnect()
        return WebService.Response(
            errorMessage = errorMessage,
            code = responseCode
        )
    }

    override fun toString(): String {
        return "WebServiceHttp(appId='$appId', apiKey='$apiKey', connectTimeoutInMilliseconds=$connectTimeoutInMilliseconds, readTimeoutInMilliseconds=$readTimeoutInMilliseconds)"
    }


}
