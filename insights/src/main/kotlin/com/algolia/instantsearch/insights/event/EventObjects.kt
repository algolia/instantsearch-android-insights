package com.algolia.instantsearch.insights.event

/**
 * An array of objects associated with this event. See [IDs] and [Filters].
 */
sealed class EventObjects(val list : List<String>) {
    /** An array of index objectID. **Limited to 20 objects.** */
    data class IDs(val iDs: List<String>): EventObjects(iDs)
    /** An array of filters. **Limited to 10 filters.** */
    data class Filters(val filters: List<String>): EventObjects(filters)
}
