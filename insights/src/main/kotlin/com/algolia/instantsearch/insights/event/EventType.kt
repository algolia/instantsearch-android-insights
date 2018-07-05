package com.algolia.instantsearch.insights.event


internal enum class EventType(val route: String) {
    Click("click"),
    View("view"),
    Conversion("conversion")
}
