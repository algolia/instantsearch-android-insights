package com.algolia.instantsearch

import com.algolia.instantsearch.insights.BuildConfig
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.IndexNameKey
import com.algolia.instantsearch.insights.webservice.WebServiceHttp


internal object TestUtils {

    val appId = BuildConfig.ALGOLIA_APPLICATION_ID
    val apiKey = BuildConfig.ALGOLIA_API_KEY
    val indexName = "latency"
    val eventParametersA get() = eventParameters("EventA")
    val eventParametersB get() = eventParameters("EventB")
    val eventParametersC get() = eventParameters("EventC")
    val configuration = Insights.Configuration(
        connectTimeoutInMilliseconds = 5000,
        readTimeoutInMilliseconds = 5000
    )

    private fun eventParameters(name: String): Map<String, Any> {
        return mapOf(
            "eventName" to name,
            "queryID" to "6de2f7eaa537fa93d8f8f05b927953b1",
            "position" to 1,
            "objectID" to "54675051",
            IndexNameKey to indexName,
            "timestamp" to System.currentTimeMillis()
        )
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
