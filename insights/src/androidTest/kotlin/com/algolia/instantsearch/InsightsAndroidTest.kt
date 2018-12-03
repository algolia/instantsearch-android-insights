package com.algolia.instantsearch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.InsightsException
import com.algolia.instantsearch.insights.event.EventUploaderAndroidJob
import com.evernote.android.job.JobManager
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class InsightsAndroidTest {

    private val context get() = InstrumentationRegistry.getContext()

    @Test
    fun testSharedWithoutRegister() {
        try {
            Insights.shared("index")
        } catch (exception: Exception) {
            assertTrue(exception is InsightsException.IndexNotRegistered)
        }
    }

    @Test
    fun testSharedAfterRegister() {
        val insights = Insights.register(context, "testApp", "testKey", "index", AndroidTestUtils.configuration)
        val insightsShared = Insights.shared("index")
        assertEquals(insights, insightsShared)
    }

    @Test
    fun testPeriodicAndroidJob() {
        (0 until 10).forEach {
            EventUploaderAndroidJob(context).startPeriodicUpload()
        }
        assertEquals(1, JobManager.instance().allJobRequests.size)
    }
}
