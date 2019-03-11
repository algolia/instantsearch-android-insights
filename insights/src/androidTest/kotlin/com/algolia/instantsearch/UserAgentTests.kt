package com.algolia.instantsearch

import android.os.Build
import com.algolia.instantsearch.insights.webservice.computeUserAgent
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.runner.AndroidJUnit4
import com.algolia.instantsearch.insights.BuildConfig
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
internal class UserAgentTest {

    @Test
    fun testUserAgent() {
        val userAgent = computeUserAgent()
        val expectedUserAgent = "insights-android (${BuildConfig.INSIGHTS_VERSION}); Android (${Build.VERSION.SDK_INT}.0.0)"

        // Expected output: insights-android (LIB_VERSION); Android (ANDROID_VERSION)
        assertEquals(expectedUserAgent, userAgent)
    }
}