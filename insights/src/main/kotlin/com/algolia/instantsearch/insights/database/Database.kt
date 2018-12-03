package com.algolia.instantsearch.insights.database

import com.algolia.instantsearch.insights.event.Event


internal interface Database {

    val indexName: String

    fun append(event: Event)

    fun overwrite(events: List<Event>)

    fun read(): List<Event>

    fun count() : Int

    fun clear()
}
