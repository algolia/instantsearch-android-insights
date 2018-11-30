package com.algolia.instantsearch

import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.IndexNameKey
import com.algolia.instantsearch.insights.event.EventType


internal object AndroidTestUtils {

    val indexName = "latency"
    val eventParametersClick get() = eventParameters("EventA", EventType.Click)
    val eventParametersConversion get() = eventParameters("EventB", EventType.Conversion)
    val eventParametersView get() = eventParameters("EventC", EventType.View)
    val configuration = Insights.Configuration(
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
}
