package com.algolia.instantsearch.insights.event

import androidx.work.*
import com.algolia.instantsearch.insights.InstantSearchInsightsException
import java.util.concurrent.TimeUnit


internal class EventUploaderWorkManager : EventUploader {

    enum class WorkerName {
        PeriodicUpload,
        OneTimeUpload
    }

    override fun startPeriodicUpload() {
        val repeatIntervalInMinutes = 15L
        val flexTimeIntervalInMinutes = 5L
        val worker = PeriodicWorkRequestBuilder<EventWorker>(
            repeatInterval = repeatIntervalInMinutes,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = flexTimeIntervalInMinutes,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).build()

        workManager {
            enqueueUniquePeriodicWork(WorkerName.PeriodicUpload.name, ExistingPeriodicWorkPolicy.KEEP, worker)
        }
    }

    override fun startOneTimeUpload() {
        val worker = OneTimeWorkRequestBuilder<EventWorker>().also {
            val backOffDelayInSeconds = 10L

            it.setBackoffCriteria(BackoffPolicy.EXPONENTIAL, backOffDelayInSeconds, TimeUnit.SECONDS)
        }.build()

        workManager {
            beginUniqueWork(WorkerName.OneTimeUpload.name, ExistingWorkPolicy.KEEP, worker).enqueue()
        }
    }

    private fun workManager(workManager: WorkManager.() -> Unit) {
        WorkManager.getInstance().let {
            if (it != null) {
                workManager(it)
            } else {
                throw InstantSearchInsightsException.ManualInitializationRequired()
            }
        }
    }
}
