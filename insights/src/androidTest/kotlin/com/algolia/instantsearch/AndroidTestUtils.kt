package com.algolia.instantsearch

import com.algolia.instantsearch.insights.BuildConfig
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.webservice.WebServiceHttp


internal object AndroidTestUtils {

    private const val eventA = "EventA"
    private const val eventB = "EventB"
    private const val eventC = "EventC"

    const val indexName = "latency"

    private const val appId = BuildConfig.ALGOLIA_APPLICATION_ID
    private const val apiKey = BuildConfig.ALGOLIA_API_KEY

    private const val queryId = "6de2f7eaa537fa93d8f8f05b927953b1"
    private const val userToken = "foobarbaz"
    private val positions = listOf(1)
    private val objectIDs = listOf("54675051")
    private val timestamp = System.currentTimeMillis()

    val configuration = Insights.Configuration(
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

    val eventConversion = Event.Conversion(
        eventB,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs
    )

    val eventView = Event.View(
        eventC,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs
    )

    val webService = WebServiceHttp(
        appId = appId,
        apiKey = apiKey,
        environment = WebServiceHttp.Environment.Prod,
        connectTimeoutInMilliseconds = configuration.connectTimeoutInMilliseconds,
        readTimeoutInMilliseconds = configuration.readTimeoutInMilliseconds
    )
}
