package com.algolia.instantsearch

import com.algolia.instantsearch.insights.Insights


internal object AndroidTestUtils {

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
            "indexName" to indexName,
            "timestamp" to System.currentTimeMillis()
        )
    }
}
