package com.algolia.instantsearch.insights

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit


class Insights internal constructor(
    context: Context,
    private val credentials: Credentials,
    private val configuration: Configuration,
    private val environment: NetworkManager.Environment
) {
    class Configuration(
        val connectTimeoutInMilliseconds: Int,
        val readTimeoutInMilliseconds: Int
    )

    private val preferences = context.sharedPreferences(credentials.indexName)

    var loggingEnabled: Boolean = false
        set(value) {
            field = value
            Logger.enabled = value
        }

    init {
        if (preferences.workerId == null) {
            val worker = PeriodicWorkRequestBuilder<WorkerEvent>(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).also {
                val inputData = WorkerEvent.buildInputData(credentials, configuration, environment)

                it.setInputData(inputData)
            }.build()
            preferences.workerId = worker.id.toString()
            WorkManager.getInstance().enqueue(worker)
        }
    }

    fun click(params: Map<String, Any>) {
        process(Event.Click(params))
    }

    private fun view(params: Map<String, Any>) {
        process(Event.View(params))
    }

    fun conversion(params: Map<String, Any>) {
        process(Event.Conversion(params))
    }

    private fun process(event: Event) {
        val events = preferences.events
            .map(ConverterStringToEvent::convert)
            .toMutableList()
            .also { it.add(event) }

        preferences.events = ConverterEventToString.convert(events).toSet()
        val worker = OneTimeWorkRequestBuilder<WorkerEvent>().also {
            val inputData = WorkerEvent.buildInputData(credentials, configuration, environment)

            it.setInputData(inputData)
            it.setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
        }.build()
        WorkManager.getInstance().enqueue(worker)
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
            val credentials = Credentials(
                appId = appId,
                apiKey = apiKey,
                indexName = indexName
            )
            val insights = Insights(context, credentials, configuration, NetworkManager.Environment.Prod)

            insightsMap[indexName] = insights
            return insights
        }

        @JvmStatic
        fun shared(indexName: String): Insights {
            return insightsMap[indexName] ?: throw InstantSearchInsightsException.CredentialsNotFound()
        }
    }
}
