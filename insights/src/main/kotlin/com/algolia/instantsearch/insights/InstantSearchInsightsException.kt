package com.algolia.instantsearch.insights


/**
 * InstantSearch Insights exceptions.
 */
sealed class InstantSearchInsightsException : Exception() {

    /**
     * This exceptions will be thrown when you try to access an index through the [Insights.shared]
     * method without having registered the index through the [Insights.register] method first.
     */
    class IndexNotRegistered : InstantSearchInsightsException()

    class ManualInitializationRequired: InstantSearchInsightsException()
}
