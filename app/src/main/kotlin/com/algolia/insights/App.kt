package com.algolia.insights

import android.app.Application
import com.algolia.instantsearch.insights.InstantSearchInsights


class App : Application() {

    private val appId = "SPH6CBEPLC"
    private val apiKey = "064f4f03e7c37d8d7cfb40cdbf852f3d"
    private val indexName = "support_rmogos"

    override fun onCreate() {
        super.onCreate()
        val configuration = InstantSearchInsights.Configuration(
            connectTimeoutInMilliseconds = 5000,
            readTimeoutInMilliseconds = 5000
        )
        InstantSearchInsights.register(applicationContext, appId, apiKey, indexName, configuration).also {
            it.loggingEnabled = true
        }
    }
}