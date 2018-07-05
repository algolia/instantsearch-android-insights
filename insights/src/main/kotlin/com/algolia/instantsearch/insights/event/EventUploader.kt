package com.algolia.instantsearch.insights.event


internal interface EventUploader {

    fun startPeriodicUpload()
    fun startOneTimeUpload()
}
