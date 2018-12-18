package com.algolia.instantsearch.insights.event

/**
 * An array of objects associated with this event. See [IDs] and [Filters].
 */
sealed class EventObjects(vararg val values: String) {
    /** An array of index objectID. **Limited to 20 objects.** */
    class IDs(vararg iDs : String): EventObjects(*iDs)
    /** An array of filters. **Limited to 10 filters.** */
    class Filters(vararg filters: String): EventObjects(*filters)
}
