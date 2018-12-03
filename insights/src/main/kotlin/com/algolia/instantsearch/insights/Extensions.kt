package com.algolia.instantsearch.insights

import org.json.JSONArray


internal fun prefixAlgolia(string: String): String = "Algolia Insights-$string"

internal fun JSONArray.toList(): List<Any> {
    return mutableListOf<Any>().also {
        for (i in 0 until this.length()) {
            it.add(this[i])
        }
    }
}
