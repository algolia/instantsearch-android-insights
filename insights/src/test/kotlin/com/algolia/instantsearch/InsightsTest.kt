package com.algolia.instantsearch

import com.algolia.instantsearch.TestUtils.click
import com.algolia.instantsearch.TestUtils.conversion
import com.algolia.instantsearch.TestUtils.eventClick
import com.algolia.instantsearch.TestUtils.eventConversion
import com.algolia.instantsearch.TestUtils.eventView
import com.algolia.instantsearch.TestUtils.indexName
import com.algolia.instantsearch.TestUtils.view
import com.algolia.instantsearch.TestUtils.webService
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.ConverterEventInternalToString
import com.algolia.instantsearch.insights.converter.ConverterEventToEventInternal
import com.algolia.instantsearch.insights.converter.ConverterStringToEventInternal
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventInternal
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.webservice.WebService
import com.algolia.instantsearch.insights.webservice.uploadEvents
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@RunWith(JUnit4::class)
class InsightsTest {
    private val responseOK = WebService.Response(null, 200)

    @Test
    fun testEventConverters() {
        val internal = ConverterEventToEventInternal.convert(eventClick)
        val string = ConverterEventInternalToString.convert(internal)
        val event = ConverterStringToEventInternal.convert(string)
        assertEquals(internal, event)
    }

    @Test
    fun testClickEvent() {
        // given an event built raw
//        assertEquals(responseOK, webService.send(firstEvent))
        // given an event built with typed constructor
        assertEquals(responseOK, webService.send(click))
    }

    @Test
    fun testViewEvent() {
        // given an event built raw
//        assertEquals(responseOK, webService.send(thirdEvent))
        // given an event built with typed constructor
        assertEquals(responseOK, webService.send(view))
    }

    @Test
    fun testConversionEvent() {
        // given an event built raw
//        assertEquals(responseOK, webService.send(secondEvent))
        // given an event built with typed constructor
        assertEquals(responseOK, webService.send(conversion))
    }

    @Test
    fun testEnabled() {
        val events = mutableListOf(click, conversion, view)
        val database = MockDatabase(indexName, events)
        val webService = MockWebService()
        val uploader = object : AssertingEventUploader(events, webService, database) {
            override fun startOneTimeUpload() {
                val trackedEvents = database.read()
                assertFalse(trackedEvents.contains(click), "The first event should have been ignored")
                assertTrue(trackedEvents.contains(conversion), "The second event should be uploaded")
            }
        }
        val insights = Insights(indexName, uploader, database, webService)
        insights.minBatchSize = 1 // Given an Insights that uploads every event

        insights.enabled = false // When a firstEvent is sent with insight disabled
        insights.personalization.click(eventClick)
        insights.enabled = true // And a secondEvent sent with insight enabled
        insights.personalization.conversion(eventConversion)
    }

    @Test
    fun testMinBatchSize() {
        val events = mutableListOf(click, conversion, view)
        val database = MockDatabase(indexName, events)
        val webService = MockWebService()
        val uploader = MinBatchSizeEventUploader(events, webService, database)
        val insights = Insights(indexName, uploader, database, webService)

        // Given a minBatchSize of one and one event
        insights.minBatchSize = 1
        insights.track(eventClick)
        // Given a minBatchSize of two and two events
        insights.minBatchSize = 2
        insights.track(eventClick)
        insights.track(eventClick)
        // Given a minBatchSize of four and four events
        insights.minBatchSize = 4
        insights.track(eventClick)
        insights.track(eventClick)
        insights.track(eventClick)
        insights.track(eventClick)
    }

    inner class MinBatchSizeEventUploader internal constructor(
        private val events: MutableList<EventInternal>,
        private val webService: MockWebService,
        private val database: MockDatabase
    ) : AssertingEventUploader(events, webService, database) {

        override fun startOneTimeUpload() {
            when (count) {
                // Expect a single event on first call
                0 -> assertEquals(1, database.count(), "startOneTimeUpload should be called first with one event")
                // Expect two events on second call
                1 -> assertEquals(2, database.count(), "startOneTimeUpload should be called second with two events")
                // Expect two events on third call
                2 -> assertEquals(4, database.count(), "startOneTimeUpload should be called third with four events")
            }

            count++
            database.clear()
        }

    }


    /**
     * Tests the integration of events, WebService and Database.
     */
    @Test
    fun testIntegration() {
        val events = mutableListOf(click, conversion, view)
        val database = MockDatabase(indexName, events)
        val webService = MockWebService()
        val uploader = IntegrationEventUploader(events, webService, database)
        val insights = Insights(indexName, uploader, database, webService)
        insights.minBatchSize = 1

        webService.code = 200 // Given a working web service
        insights.track(eventClick)
        webService.code = -1 // Given a web service that errors
        insights.track(eventConversion)
        webService.code = 400 // Given a working web service returning an HTTP error
        insights.track(eventView) // When tracking an event

        webService.code = -1 // Given a web service that errors
        insights.userToken = TestUtils.eventClick.userToken // Given an userToken

        // When adding events without explicitly-provided userToken
        insights.search.click(eventClick.eventName, eventClick.indexName, eventClick.timestamp, eventClick.queryId!!, eventClick.objectIDs, eventClick.positions)
        insights.personalization.click(eventClick.eventName, eventClick.indexName, eventClick.timestamp, eventClick.queryId, eventClick.objectIDs)
        insights.personalization.conversion(eventConversion.eventName, eventConversion.indexName, eventConversion.timestamp, eventConversion.queryId, eventConversion.objectIDs)
        webService.code = 200 // Given a working web service
        insights.personalization.view(eventView)
    }

    inner class IntegrationEventUploader internal constructor(
        private val events: MutableList<EventInternal>,
        private val webService: MockWebService,
        private val database: MockDatabase
    ) : AssertingEventUploader(events, webService, database) {
        override fun startOneTimeUpload() {
            val clickEventNotForSearch = Event.Click(
                eventName = eventClick.eventName,
                indexName = eventClick.indexName,
                userToken = eventClick.userToken,
                timestamp = eventClick.timestamp,
                queryId = eventClick.queryId,
                objectIDs = eventClick.objectIDs,
                positions = null
            )
            val clickEventInternal = ConverterEventToEventInternal.convert(clickEventNotForSearch)// A Click event not for Search has no positions

            when (count) {
                0 -> assertEquals(listOf(click), database.read(), "failed 0") // expect added first
                1 -> assertEquals(listOf(conversion), database.read(), "failed 1") // expect flush then added second
                2 -> assertEquals(listOf(conversion, view), database.read(), "failed 2")

                3 -> assertEquals(listOf(click), database.read(), "failed 3") // expect flush then added first
                4 -> assertEquals(listOf(click, clickEventInternal), database.read(), "failed 4") // expect added first
                5 -> assertEquals(listOf(click, clickEventInternal, conversion), database.read(), "failed 5") // expect added second
                6 -> assertEquals(listOf(click, clickEventInternal, conversion, view), database.read(), "failed 6") // expect added third

            }
            webService.uploadEvents(database, TestUtils.indexName)
            when (count) {
                0 -> assert(database.read().isEmpty()) // expect flushed first
                1 -> assertEquals(listOf(conversion), database.read()) // expect kept second
                2 -> assert(database.read().isEmpty()) // expect flushed events

                3 -> assertEquals(listOf(click), database.read()) // expect kept first
                4 -> assertEquals(listOf(click, clickEventInternal), database.read()) // expect kept first2
                5 -> assertEquals(listOf(click, clickEventInternal, conversion), database.read()) // expect kept second
                6 -> assert(database.read().isEmpty()) // expect flushed events
            }
            count++
        }
    }

    abstract inner class AssertingEventUploader internal constructor(
        private val events: MutableList<EventInternal>,
        private val webService: MockWebService,
        private val database: MockDatabase
    ) : EventUploader {

        protected var count: Int = 0

        override fun startPeriodicUpload() {
            assertEquals(events, database.read())
            webService.uploadEvents(database, TestUtils.indexName)
            assert(database.read().isEmpty())
        }

    }
}
