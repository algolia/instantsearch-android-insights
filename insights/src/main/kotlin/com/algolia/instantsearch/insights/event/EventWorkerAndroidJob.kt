package com.algolia.instantsearch.insights.event

import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.InsightsLogger
import com.algolia.instantsearch.insights.webservice.uploadEvents
import com.evernote.android.job.Job


internal class EventWorkerAndroidJob : Job() {

    override fun onRunJob(params: Params): Result {
        InsightsLogger.log("Worker started with indices ${Insights.insightsMap.keys}.")
        val hasAnyEventFailed = Insights.insightsMap
            .map { it.value.webService.uploadEvents(it.value.database, it.key).isEmpty() }
            .any { !it }
        val result = if (hasAnyEventFailed) Job.Result.FAILURE else Job.Result.SUCCESS
        InsightsLogger.log("Worker ended with result: $result.")
        return result
    }
}
