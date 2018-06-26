package com.algolia.instantsearch

import com.algolia.instantsearch.insights.database.Database
import com.algolia.instantsearch.insights.event.Event


internal class MockDatabase(
    override val indexName: String,
    private val events: MutableList<Event>
) : Database {

    override fun append(event: Event) {
        events.add(event)
    }

    override fun overwrite(events: List<Event>) {
        clear()
        this.events += events
    }

    override fun read(): List<Event> {
        return events
    }

    override fun clear() {
        events.clear()
    }
}
