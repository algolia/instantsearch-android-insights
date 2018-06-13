package com.algolia.insights

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.algolia.instantsearch.insights.InstantSearchInsights


class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val insights = InstantSearchInsights.shared("support_rmogos")

        insights.loggingEnabled = true

        insights.click(eventParameters("EventA"))
    }

    private fun eventParameters(name: String): Map<String, Any> {
        return mapOf(
            "eventName" to name,
            "queryID" to "6de2f7eaa537fa93d8f8f05b927953b1",
            "position" to 1,
            "objectID" to "54675051",
            "indexName" to "support_rmogos",
            "timestamp" to System.currentTimeMillis()
        )
    }
}