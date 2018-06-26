package com.algolia.instantsearch.insights

import android.content.Context
import com.algolia.instantsearch.insights.database.Database
import com.algolia.instantsearch.insights.database.DatabaseSharedPreferences
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.event.EventUploaderWorkManager
import com.algolia.instantsearch.insights.webservice.WebService
import com.algolia.instantsearch.insights.webservice.WebServiceHttp


class Insights internal constructor(
    private val indexName: String,
    private val eventUploader: EventUploader,
    val database: Database,
    val webService: WebService
) {

    class Configuration(
        val connectTimeoutInMilliseconds: Int,
        val readTimeoutInMilliseconds: Int
    )

    var loggingEnabled: Boolean = false
        set(value) {
            field = value
            InsightsLogger.enabled[indexName] = value
        }

    init {
        eventUploader.startPeriodicUpload()
    }

    fun click(params: Map<String, Any>) {
        process(Event.Click(params))
    }

    internal fun view(params: Map<String, Any>) {
        process(Event.View(params))
    }

    fun conversion(params: Map<String, Any>) {
        process(Event.Conversion(params))
    }

    private fun process(event: Event) {
        database.append(event)
        eventUploader.startOneTimeUpload()
    }

    companion object {

        private val insightsMap = mutableMapOf<String, Insights>()

        @JvmStatic
        fun register(
            context: Context,
            appId: String,
            apiKey: String,
            indexName: String,
            configuration: Configuration
        ): Insights {
            val eventUploader = EventUploaderWorkManager(context, indexName)
            val database = DatabaseSharedPreferences(context, indexName)
            val webService = WebServiceHttp(
                appId = appId,
                apiKey = apiKey,
                environment = WebServiceHttp.Environment.Prod,
                connectTimeoutInMilliseconds = configuration.connectTimeoutInMilliseconds,
                readTimeoutInMilliseconds = configuration.readTimeoutInMilliseconds
            )
            val insights = Insights(indexName, eventUploader, database, webService)

            insightsMap[indexName] = insights
            return insights
        }

        @JvmStatic
        fun shared(indexName: String): Insights {
            return insightsMap[indexName]
                ?: throw InstantSearchInsightsException.CredentialsNotFound()
        }
    }
}
