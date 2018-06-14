package com.algolia.instantsearch.insights

import java.net.HttpURLConnection
import java.net.URL


internal class NetworkManager(
    private val appId: String,
    private val apiKey: String,
    private val environment: Environment,
    private val configuration: InstantSearchInsights.Configuration
) {

    enum class Environment(private val baseUrl: String) {
        Prod("https://insights.algolia.io"),
        Debug("http://localhost:8080");

        fun buildUrl(eventType: EventType): String {
            return "$baseUrl/1/searches/${eventType.route}"
        }
    }

    data class Response(
        val errorMessage: String?,
        val code: Int
    )

    fun sendEvent(event: Event): Response {
        val eventType = when (event) {
            is Event.Click -> EventType.Click
            is Event.View -> EventType.View
            is Event.Conversion -> EventType.Conversion
        }
        val string = ConverterParameterToString.convert(event.params)
        val url = URL(environment.buildUrl(eventType))
        val connection = (url.openConnection() as HttpURLConnection).also {
            it.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            it.setRequestProperty("Accept", "application/json")
            it.setRequestProperty("X-Algolia-Application-Id", appId)
            it.setRequestProperty("X-Algolia-API-Key", apiKey)
            it.setRequestProperty("Content-Length", string.length.toString())
            it.requestMethod = "POST"
            it.connectTimeout = configuration.connectTimeoutInMilliseconds
            it.readTimeout = configuration.readTimeoutInMilliseconds
            it.doOutput = true
            it.useCaches = false
        }
        connection.outputStream.write(string.toByteArray())
        val responseCode = connection.responseCode
        val errorMessage = connection.errorStream?.bufferedReader()?.readText()
        connection.disconnect()
        return Response(
            errorMessage = errorMessage,
            code = responseCode
        )
    }
}
