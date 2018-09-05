package com.algolia.instantsearch.insights.event

import android.content.Context
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import java.util.concurrent.TimeUnit


internal class EventUploaderAndroidJob(context: Context) : EventUploader {

    private val preferences = context.getSharedPreferences("Insights", Context.MODE_PRIVATE)

    private enum class Preference {
        JobId
    }

    companion object {

        private const val repeatIntervalInMinutes = 15L
        private const val flexTimeIntervalInMinutes = 5L
        private const val defaultJobId = -1
    }

    init {
        JobManager.create(context).addJobCreator(EventJobCreator())
    }

    override fun startPeriodicUpload() {
        val storedJobId = preferences.getInt(Preference.JobId.name, defaultJobId)
        if (storedJobId == defaultJobId) {
            val jobId = JobRequest
                .Builder(EventJobCreator.Tag.Periodic.name)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setPeriodic(
                    TimeUnit.MINUTES.toMillis(repeatIntervalInMinutes),
                    TimeUnit.MINUTES.toMillis(flexTimeIntervalInMinutes))
                .build()
                .schedule()
            preferences.edit().putInt(Preference.JobId.name, jobId).apply()
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
