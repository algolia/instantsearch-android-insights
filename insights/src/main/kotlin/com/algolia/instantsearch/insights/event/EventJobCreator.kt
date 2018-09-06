package com.algolia.instantsearch.insights.event

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator


internal class EventJobCreator : JobCreator {

    enum class Tag {
        OneTime,
        Periodic,
    }

    override fun create(tag: String): Job? {
        return when (Tag.valueOf(tag)) {
            Tag.OneTime, Tag.Periodic -> EventWorkerAndroidJob()
        }
    }
}
