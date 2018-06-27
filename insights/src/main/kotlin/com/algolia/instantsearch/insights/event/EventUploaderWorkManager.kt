package com.algolia.instantsearch.insights.event

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.algolia.instantsearch.insights.database.sharedPreferences
import com.algolia.instantsearch.insights.database.workerId
import java.util.concurrent.TimeUnit


internal class EventUploaderWorkManager(
    context: Context,
    override val indexName: String
) : EventUploader {

    private val preferences = context.sharedPreferences(indexName)

    override fun startPeriodicUpload() {
        if (preferences.workerId == null) {
            val repeatIntervalInMinutes = 15L
            val flexTimeIntervalInMinutes = 5L
            val worker = PeriodicWorkRequestBuilder<EventWorker>(
                repeatInterval = repeatIntervalInMinutes,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
                flexTimeInterval = flexTimeIntervalInMinutes,
                flexTimeIntervalUnit = TimeUnit.MINUTES
            ).also {
                val inputData = EventWorker.buildInputData(indexName)

                it.setInputData(inputData)
            }.build()
            preferences.workerId = worker.id.toString()
            WorkManager.getInstance().enqueue(worker)
        }
    }

    override fun startOneTimeUpload() {
        val worker = OneTimeWorkRequestBuilder<EventWorker>().also {
            val inputData = EventWorker.buildInputData(indexName)
            val backOffDelayInSeconds = 10L

            it.setInputData(inputData)
            it.setBackoffCriteria(BackoffPolicy.EXPONENTIAL, backOffDelayInSeconds, TimeUnit.SECONDS)
        }.build()
        WorkManager.getInstance().enqueue(worker)
    }
}
