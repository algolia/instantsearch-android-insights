package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.EventInternal
import org.json.JSONArray
import org.json.JSONObject


internal object ConverterEventInternalToString : Converter<EventInternal, String> {

    override fun convert(input: EventInternal): String {
        return JSONObject().apply {
            input.entries.forEach { entry ->
                entry.value?.let {
                    if (it is Collection<*>) {
                        put(entry.key, JSONArray(it))
                    } else {
                        put(entry.key, it)
                    }
                }
            }
        }.toString()
    }
}