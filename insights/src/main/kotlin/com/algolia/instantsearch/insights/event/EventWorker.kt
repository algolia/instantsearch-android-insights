package com.algolia.instantsearch.insights.event

import androidx.work.Worker
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.InsightsLogger
import com.algolia.instantsearch.insights.webservice.uploadEvents


internal class EventWorker : Worker() {

    override fun doWork(): WorkerResult {
        InsightsLogger.log("Worker started with indices ${Insights.insightsMap.keys}.")
        val hasAnyEventFailed = Insights.insightsMap
            .map { it.value.webService.uploadEvents(it.value.database, it.key).isEmpty() }
            .any { !it }
        val result = if (hasAnyEventFailed) WorkerResult.RETRY else WorkerResult.SUCCESS
        InsightsLogger.log("Worker ended with result: $result.")
        return result
    }
}
