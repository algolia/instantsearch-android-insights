package com.algolia.instantsearch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.algolia.instantsearch.insights.converter.ConverterEventToEventInternal
import com.algolia.instantsearch.insights.database.DatabaseSharedPreferences
import com.algolia.instantsearch.insights.event.Event
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class AndroidTestDatabaseSharedPreferences {

    private val context get() = InstrumentationRegistry.getContext()
    private val eventA = "EventA"
    private val eventB = "EventB"
    private val eventC = "EventC"
    private val indexName = "latency"
    private val queryId = "6de2f7eaa537fa93d8f8f05b927953b1"
    private val userToken = "foobarbaz"
    private val positions = listOf(1)
    private val objectIDs = listOf("54675051")
    private val timestamp = System.currentTimeMillis()
    private val eventClick = Event.Click(
        eventA,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs,
        positions
    )
    private val eventConversion = Event.Conversion(
        eventB,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs
    )
    private val eventView = Event.View(
        eventC,
        indexName,
        userToken,
        timestamp,
        queryId,
        objectIDs
    )
    private val click = ConverterEventToEventInternal.convert(eventClick)
    private val view = ConverterEventToEventInternal.convert(eventView)
    private val conversion = ConverterEventToEventInternal.convert(eventConversion)

    @Test
    fun test() {
        val events = listOf(
            click,
            conversion
        )
        val database = DatabaseSharedPreferences(context, indexName)

        database.overwrite(events)

        assertTrue(database.read().containsAll(events))

        database.append(view)

        assertTrue(database.read().containsAll(events.plus(view)))
    }
}
