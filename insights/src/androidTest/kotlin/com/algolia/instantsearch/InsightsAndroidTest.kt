package com.algolia.instantsearch

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.InsightsException
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class InsightsAndroidTest {

    private val context get() = ApplicationProvider.getApplicationContext<Context>()
    private val configuration = Insights.Configuration(
        connectTimeoutInMilliseconds = 5000,
        readTimeoutInMilliseconds = 5000
    )

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
        val insights = Insights.register(context, "testApp", "testKey", "index", configuration)
        val insightsShared = Insights.shared
        assertEquals(insights, insightsShared)
    }

    @Test
    fun testSharedNamedAfterRegister() {
        val insights = Insights.register(context, "testApp", "testKey", "index", configuration)
        val insightsShared = Insights.shared("index")
        assertEquals(insights, insightsShared)
    }

    @Test
    fun testRegisterGlobalUserToken() {
        val insights = Insights.register(context, "testApp", "testKey", "index", configuration)
        val insightsShared = Insights.shared("index")
        assertEquals(configuration.defaultUserToken, insightsShared.userToken)
    }
}
