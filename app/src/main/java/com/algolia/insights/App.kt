package com.algolia.insights

import android.app.Application
import com.algolia.instantsearch.insights.Insights
import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.insights.UserToken
import com.facebook.stetho.Stetho

class App : Application() {
    lateinit var insights: Insights
    lateinit var client: ClientSearch
    lateinit var index: Index
    override fun onCreate() {
        super.onCreate()
        val applicationID = ApplicationID("latency")
        val apiKey = APIKey("afc3dd66dd1293e2e2736a5a51b05c0a")
        val indexName = IndexName("indexName")
        val configuration = Insights.Configuration(5000, 5000)
        insights = Insights.register(this, applicationID, apiKey, indexName, configuration).apply {
            loggingEnabled = true
            userToken = UserToken("userToken")
            minBatchSize = 1
        }
        client = ClientSearch(applicationID, apiKey)
        index = client.initIndex(indexName)
        Stetho.initializeWithDefaults(this)
    }
}
