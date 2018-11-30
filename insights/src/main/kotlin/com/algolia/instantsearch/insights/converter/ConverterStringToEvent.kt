package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventType
import com.algolia.instantsearch.insights.webservice.toList
import org.json.JSONArray
import org.json.JSONObject


internal object ConverterStringToEvent : Converter<String, Event> {

    override fun convert(input: String): Event {
        val json = JSONObject(input)
        val type = EventType.valueOf(json[EventTypeKey].toString().capitalize())
        val params: Map<String, Any> = json.keys()
            .asSequence()
            .filterNot { it == EventTypeKey }
            .map { it to if (json.get(it) is JSONArray) json.getJSONArray(it).toList() else json.get(it) }
            .toMap()

        return when (type) {
            EventType.View -> Event.View(params)
            EventType.Click -> Event.Click(params)
            EventType.Conversion -> Event.Conversion(params)
        }
    }
}
