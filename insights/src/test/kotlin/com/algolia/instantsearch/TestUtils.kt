package com.algolia.instantsearch

import com.algolia.instantsearch.insights.BuildConfig
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.Event.Companion.EventNameKey
import com.algolia.instantsearch.insights.event.Event.Companion.IndexNameKey
import com.algolia.instantsearch.insights.event.Event.Companion.ObjectIDsKey
import com.algolia.instantsearch.insights.event.Event.Companion.PositionsKey
import com.algolia.instantsearch.insights.event.Event.Companion.QueryIdKey
import com.algolia.instantsearch.insights.event.Event.Companion.TimestampKey
import com.algolia.instantsearch.insights.event.Event.Companion.UserTokenKey
import com.algolia.instantsearch.insights.event.EventType
import com.algolia.instantsearch.insights.webservice.WebServiceHttp


internal object TestUtils {

    val eventParametersClick get() = eventParameters("EventA", EventType.Click)
    val eventParametersConversion get() = eventParameters("EventB", EventType.Conversion)
    val eventParametersView get() = eventParameters("EventC", EventType.View)

    val eventView
        get() = Event.View(
            eventParametersView[EventNameKey] as String,
            eventParametersView[IndexNameKey] as String,
            eventParametersView[UserTokenKey] as String,
            eventParametersView[TimestampKey] as Long,
            eventParametersView[QueryIdKey] as String?,
            if (eventParametersView[ObjectIDsKey] is List<*>) eventParametersView[ObjectIDsKey] as List<String>? else null
        )

    val eventClick
        get() = Event.Click(
            eventParametersClick[EventNameKey] as String,
            eventParametersClick[IndexNameKey] as String,
            eventParametersClick[UserTokenKey] as String,
            eventParametersClick[TimestampKey] as Long,
            eventParametersClick[QueryIdKey] as String,
            if (eventParametersClick[ObjectIDsKey] is List<*>) eventParametersClick[ObjectIDsKey] as List<String>? else null,
            if (eventParametersClick[PositionsKey] is List<*>) eventParametersClick[PositionsKey] as List<Int>? else null
        )

    val eventConversion
        get() = Event.Conversion(
            eventParametersConversion[EventNameKey] as String,
            eventParametersConversion[IndexNameKey] as String,
            eventParametersConversion[UserTokenKey] as String,
            eventParametersConversion[TimestampKey] as Long,
            eventParametersConversion[QueryIdKey] as String?,
            if (eventParametersConversion[ObjectIDsKey] is List<*>) eventParametersConversion[ObjectIDsKey] as List<String>? else null
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
