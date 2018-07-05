package com.algolia.instantsearch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.InstantSearchInsightsException
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class InsightsAndroidTest {

    private val context get() = InstrumentationRegistry.getContext()

    @Test
    fun testInitShouldFail() {
        try {
            Insights.shared("index")
        } catch (exception: Exception) {
            assertTrue(exception is InstantSearchInsightsException.CredentialsNotFound)
        }
    }

    @Test
    fun testInitShouldWork() {
        val insights = Insights.register(context, "testApp", "testKey", "index", AndroidTestUtils.configuration)
        val insightsShared = Insights.shared("index")
        assertEquals(insights, insightsShared)
    }
}
