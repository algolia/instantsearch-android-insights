package com.algolia.instantsearch.insights


/**
 * InstantSearch Insights exceptions.
 */
sealed class InstantSearchInsightsException : Exception() {

    /**
     * Will be thrown when you try to access an index through the [Insights.shared]
     * method without having registered the index through the [Insights.register] method first.
     */
    class IndexNotRegistered : InstantSearchInsightsException()

    /**
     * Will be thrown when you call [`insights.{search,personalization}.{view,click,conversion}()`][Insights.search] without [setting an userToken][Insights.userToken] first.
     */
    class NoUserToken : InstantSearchInsightsException()
}