package com.algolia.instantsearch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.algolia.instantsearch.AndroidTestUtils.click
import com.algolia.instantsearch.AndroidTestUtils.conversion
import com.algolia.instantsearch.AndroidTestUtils.view
import com.algolia.instantsearch.insights.database.DatabaseSharedPreferences
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class AndroidTestDatabaseSharedPreferences {

    private val context get() = InstrumentationRegistry.getContext()

    @Test
    fun test() {
        val events = listOf(
            click,
            conversion
        )
        val database = DatabaseSharedPreferences(context, AndroidTestUtils.indexName)

        database.overwrite(events)

        assertTrue(database.read().containsAll(events))

        database.append(view)

        assertTrue(database.read().containsAll(events.plus(view)))
    }
}
