package com.algolia.instantsearch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.algolia.instantsearch.insights.database.DatabaseSharedPreferences
import com.algolia.instantsearch.insights.event.Event
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals


@RunWith(AndroidJUnit4::class)
class AndroidTestDatabaseSharedPreferences {

    private val context get() = InstrumentationRegistry.getContext()

    @Test
    fun test() {
        val events = listOf(
            AndroidTestUtils.eventClick,
            AndroidTestUtils.eventConversion
        )
        val database = DatabaseSharedPreferences(context, AndroidTestUtils.indexName)

        database.overwrite(events)

        assertEquals(events, database.read())

        val eventC = Event.Conversion(AndroidTestUtils.eventView.params)

        database.append(eventC)

        assertEquals(listOf(*events.toTypedArray(), eventC), database.read())
    }
}
