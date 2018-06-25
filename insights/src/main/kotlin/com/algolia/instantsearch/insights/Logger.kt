package com.algolia.instantsearch.insights

import android.util.Log


internal object Logger {

    var enabled: MutableMap<String, Boolean> = mutableMapOf()

    fun log(indexName: String, message: String) {
        if (enabled.getOrDefault(indexName, false)) {
            Log.d("Algolia Insights", "index=$indexName: $message")
        }
    }
}
