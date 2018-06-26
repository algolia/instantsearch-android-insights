package com.algolia.instantsearch.insights.event

import androidx.work.Data
import androidx.work.Worker
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.webservice.uploadEvents


internal class EventWorker : Worker() {

    private enum class Keys {
        Index
    }

    companion object {

        fun buildInputData(indexName: String): Data {
            return Data.Builder().putAll(
                mapOf(Keys.Index.name to indexName)
            ).build()
        }

        fun Data.getInputData(): String {
            return getString(Keys.Index.name, null)
        }
    }

    override fun doWork(): WorkerResult {
        val indexName = inputData.getInputData()
        val insights = Insights.shared(indexName)
        val failedEvents = insights.webService.uploadEvents(insights.database, indexName)

        return if (failedEvents.isEmpty()) WorkerResult.SUCCESS else WorkerResult.RETRY
    }
}
