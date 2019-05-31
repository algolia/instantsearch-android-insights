package com.algolia.instantsearch.insights.event

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.InsightsLogger
import com.algolia.instantsearch.insights.webservice.uploadEvents


internal class EventWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    override fun doWork(): Result {
        InsightsLogger.log("Worker started with indices ${Insights.insightsMap.keys}.")
        val hasAnyEventFailed = Insights.insightsMap
            .map { it.value.webService.uploadEvents(it.value.database, it.key).isEmpty() }
            .any { !it }
        val result = if (hasAnyEventFailed) Result.retry() else Result.success()
        InsightsLogger.log("Worker ended with result: $result.")
        return result
    }
}
