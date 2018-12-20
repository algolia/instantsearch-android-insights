package com.algolia.instantsearch.insights

import android.util.Log


internal object InsightsLogger {

    private const val tag = "Algolia Insights"
    var enabled: MutableMap<String, Boolean> = mutableMapOf()

    fun log(indexName: String, message: String) {
        if (enabled[indexName] == true) {
            Log.d(tag, "Index=$indexName: $message")
        }
    }

    fun log(message: String) {
        Log.d(tag, message)
    }
}
