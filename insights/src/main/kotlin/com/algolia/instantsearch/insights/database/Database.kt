package com.algolia.instantsearch.insights.database

import com.algolia.instantsearch.insights.event.EventInternal


internal interface Database {

    val indexName: String

    fun append(event: EventInternal)

    fun overwrite(events: List<EventInternal>)

    fun read(): List<EventInternal>

    fun count(): Int

    fun clear()
}
