package com.algolia.instantsearch.insights.event

import android.content.Context
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import java.util.concurrent.TimeUnit


internal class EventUploaderEvernote(context: Context) : EventUploader {

    private val preferences = context.getSharedPreferences("Insights", Context.MODE_PRIVATE)
    private val keyUniquePeriodicJob = "PeriodicJob"

    init {
        JobManager.create(context).addJobCreator(EventJobCreator())
    }

    override fun startPeriodicUpload() {
        val storedId = preferences.getInt(keyUniquePeriodicJob, -1)
        if (storedId == -1) {
            val repeatIntervalInMinutes = 15L
            val flexTimeIntervalInMinutes = 5L
            val id = JobRequest
                .Builder(EventJobCreator.Tag.Periodic.name)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setPeriodic(
                    TimeUnit.MINUTES.toMillis(repeatIntervalInMinutes),
                    TimeUnit.MINUTES.toMillis(flexTimeIntervalInMinutes))
                .build()
                .schedule()
            preferences.edit().putInt(keyUniquePeriodicJob, id).apply()
        }
    }

    override fun startOneTimeUpload() {
        JobRequest
            .Builder(EventJobCreator.Tag.OneTime.name)
            .startNow()
            .setUpdateCurrent(true)
            .build()
            .schedule()
    }
}
