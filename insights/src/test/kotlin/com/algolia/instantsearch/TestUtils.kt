package com.algolia.instantsearch

import com.algolia.instantsearch.insights.BuildConfig
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.Event.Companion.IndexNameKey
import com.algolia.instantsearch.insights.event.EventType
import com.algolia.instantsearch.insights.webservice.WebServiceHttp


internal object TestUtils {

    val eventParametersClick get() = eventParameters("EventA", EventType.Click)
    val eventParametersConversion get() = eventParameters("EventB", EventType.Conversion)
    val eventParametersView get() = eventParameters("EventC", EventType.View)

    val eventView
        get() = Event.View(
            eventParametersView["eventName"] as String,
            eventParametersView[IndexNameKey] as String,
            eventParametersView["userToken"] as String,
            eventParametersView["timestamp"] as Long,
            eventParametersView["queryId"] as String?,
            if (eventParametersView["objectIDs"] is List<*>) eventParametersView["objectIDs"] as List<String>? else null,
            if (eventParametersView["positions"] is List<*>) eventParametersView["positions"] as List<Int>? else null
        )

    val eventClick
        get() = Event.Click(
            eventParametersClick["eventName"] as String,
            eventParametersClick[IndexNameKey] as String,
            eventParametersClick["userToken"] as String,
            eventParametersClick["timestamp"] as Long,
            eventParametersClick["queryId"] as String?,
            if (eventParametersClick["objectIDs"] is List<*>) eventParametersClick["objectIDs"] as List<String>? else null
        )

    val eventConversion
        get() = Event.Conversion(
            eventParametersConversion["eventName"] as String,
            eventParametersConversion[IndexNameKey] as String,
            eventParametersConversion["userToken"] as String,
            eventParametersConversion["timestamp"] as Long,
            eventParametersConversion["queryId"] as String?,
            if (eventParametersConversion["objectIDs"] is List<*>) eventParametersConversion["objectIDs"] as List<String>? else null
        )

    private val appId = BuildConfig.ALGOLIA_APPLICATION_ID
    private val apiKey = BuildConfig.ALGOLIA_API_KEY
    const val indexName = "latency"
    private val configuration = Insights.Configuration(
        connectTimeoutInMilliseconds = 5000,
        readTimeoutInMilliseconds = 5000
    )

    private fun eventParameters(name: String, type: EventType): Map<String, Any> {
        return listOfNotNull(
            "eventName" to name,
            "queryID" to "6de2f7eaa537fa93d8f8f05b927953b1",
            if (type == EventType.Click) "positions" to listOf(1) else null,
            "objectIDs" to listOf("54675051"),
            "userToken" to "foobarbaz",
            IndexNameKey to indexName,
            "timestamp" to System.currentTimeMillis()
        ).toMap()
    }

    val webService
        get() = WebServiceHttp(
            appId = appId,
            apiKey = apiKey,
            environment = WebServiceHttp.Environment.Prod,
            connectTimeoutInMilliseconds = configuration.connectTimeoutInMilliseconds,
            readTimeoutInMilliseconds = configuration.readTimeoutInMilliseconds
        )
}
