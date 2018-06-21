package com.algolia.instantsearch.insights


sealed class InstantSearchInsightsException : Exception() {

    class CredentialsNotFound : InstantSearchInsightsException()
}
