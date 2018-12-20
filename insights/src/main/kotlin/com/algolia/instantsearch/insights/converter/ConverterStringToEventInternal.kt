package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.EventInternal
import com.algolia.instantsearch.insights.toList
import org.json.JSONArray
import org.json.JSONObject


internal object ConverterStringToEventInternal : Converter<String, EventInternal> {

    override fun convert(input: String): EventInternal {
        val json = JSONObject(input)
        val eventInternal = json.keys()
            .asSequence()
            .map { it to if (json.get(it) is JSONArray) json.getJSONArray(it).toList() else json.get(it) }
            .toMap()

        return eventInternal
    }
}