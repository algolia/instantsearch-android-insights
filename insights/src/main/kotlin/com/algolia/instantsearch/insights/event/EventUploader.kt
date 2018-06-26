package com.algolia.instantsearch.insights.event


internal interface EventUploader {

    val indexName: String

    fun startPeriodicUpload()
    fun startOneTimeUpload()
}
