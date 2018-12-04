package com.algolia.instantsearch

import com.algolia.instantsearch.insights.BuildConfig
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.ConverterEventToEventInternal
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.webservice.WebServiceHttp


internal object TestUtils {

    const val eventA = "EventA"
    const val eventB = "EventB"
    const val eventC = "EventC"

    const val indexName = "latency"

    private const val appId = BuildConfig.ALGOLIA_APPLICATION_ID
    private const val apiKey = BuildConfig.ALGOLIA_API_KEY

    const val queryId = "6de2f7eaa537fa93d8f8f05b927953b1"
    const val userToken = "foobarbaz"
    val positions = listOf(1)
    val objectIDs = listOf("54675051")
    val timestamp = System.currentTimeMillis()

    private val configuration = Insights.Configuration(
        connectTimeoutInMilliseconds = 5000,
        readTimeoutInMilliseconds = 5000
    )

    val eventClick = Event.Click(
        eventA,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs,
        positions
    )

    val click = ConverterEventToEventInternal.convert(eventClick)

    val eventConversion = Event.Conversion(
        eventB,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs
    )

    val conversion = ConverterEventToEventInternal.convert(eventConversion)

    val eventView = Event.View(
        eventC,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs
    )

    val view = ConverterEventToEventInternal.convert(eventView)

    val webService
        get() = WebServiceHttp(
            appId = appId,
            apiKey = apiKey,
            environment = WebServiceHttp.Environment.Prod,
            connectTimeoutInMilliseconds = configuration.connectTimeoutInMilliseconds,
            readTimeoutInMilliseconds = configuration.readTimeoutInMilliseconds
        )
}
