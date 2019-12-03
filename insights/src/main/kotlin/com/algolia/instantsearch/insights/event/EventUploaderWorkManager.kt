package com.algolia.instantsearch.insights.event

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit


internal class EventUploaderWorkManager(private val context: Context) : EventUploader {

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

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(WorkerName.PeriodicUpload.name, ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    override fun startOneTimeUpload() {
        val worker = OneTimeWorkRequestBuilder<EventWorker>().also {
            val backOffDelayInSeconds = 10L

            it.setBackoffCriteria(BackoffPolicy.EXPONENTIAL, backOffDelayInSeconds, TimeUnit.SECONDS)
        }.build()

        WorkManager
            .getInstance(context)
            .beginUniqueWork(WorkerName.OneTimeUpload.name, ExistingWorkPolicy.KEEP, worker)
            .enqueue()
    }
}