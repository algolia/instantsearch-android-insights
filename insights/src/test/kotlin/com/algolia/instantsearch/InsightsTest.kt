package com.algolia.instantsearch

import com.algolia.instantsearch.TestUtils.eventClick
import com.algolia.instantsearch.TestUtils.eventConversion
import com.algolia.instantsearch.TestUtils.eventView
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.ConverterEventToString
import com.algolia.instantsearch.insights.converter.ConverterParameterToString
import com.algolia.instantsearch.insights.converter.ConverterStringToEvent
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.webservice.WebService
import com.algolia.instantsearch.insights.webservice.uploadEvents
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@RunWith(JUnit4::class)
class InsightsTest {
    private val responseOK = WebService.Response(null, 200)
    private var firstEvent = Event.Click(TestUtils.eventParametersClick)
    private var secondEvent = Event.Conversion(TestUtils.eventParametersConversion)
    private var thirdEvent = Event.View(TestUtils.eventParametersView)

    @Test
    fun testEventConverters() {
        val string = ConverterEventToString.convert(firstEvent)
        val event = ConverterStringToEvent.convert(string)
        assertEquals(firstEvent, event)
    }

    @Test
    fun testParameterConverter() {
        val string = ConverterParameterToString.convert(firstEvent.params)
        firstEvent.params.entries.forEach {
            assertTrue(string.contains(Regex("\"${it.key}\":[[\"]?${it.value}[\"]]?")),
                "The string should contain the firstEvent's ${it.key}: $string.")
        }
    }

    @Test
    fun testClickEvent() {
        // given an event built raw
        assertEquals(responseOK, TestUtils.webService.send(firstEvent))
        // given an event built with typed constructor
        assertEquals(responseOK, TestUtils.webService.send(eventClick))
    }

    @Test
    fun testViewEvent() {
        // given an event built raw
        assertEquals(responseOK, TestUtils.webService.send(thirdEvent))
        // given an event built with typed constructor
        assertEquals(responseOK, TestUtils.webService.send(eventView))
    }

    @Test
    fun testConversionEvent() {
        // given an event built raw
        assertEquals(responseOK, TestUtils.webService.send(secondEvent))
        // given an event built with typed constructor
        assertEquals(responseOK, TestUtils.webService.send(eventConversion))
    }

    @Test
    fun testEnabled() {
        val events = mutableListOf(firstEvent, secondEvent, thirdEvent)
        val database = MockDatabase(TestUtils.indexName, events)
        val webService = MockWebService()
        val uploader = object : AssertingEventUploader(events, webService, database) {
            override fun startOneTimeUpload() {
                val trackedEvents = database.read()
                assertFalse(trackedEvents.contains(firstEvent), "The first event should have been ignored")
                assertTrue(trackedEvents.contains(secondEvent), "The second event should be uploaded")
            }
        }
        val insights = Insights(TestUtils.indexName, uploader, database, webService)
        insights.minBatchSize = 1 // Given an Insights that uploads every event

        insights.enabled = false // When a firstEvent is sent with insight disabled
        insights.personalization.click(firstEvent)
        insights.enabled = true // And a secondEvent sent with insight enabled
        insights.personalization.conversion(secondEvent)
    }

    @Test
    fun testMinBatchSize() {
        val events = mutableListOf(firstEvent, secondEvent, thirdEvent)
        val database = MockDatabase(TestUtils.indexName, events)
        val webService = MockWebService()
        val uploader = MinBatchSizeEventUploader(events, webService, database)
        val insights = Insights(TestUtils.indexName, uploader, database, webService)

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
        private val events: MutableList<Event>,
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
        val events = mutableListOf(firstEvent, secondEvent, thirdEvent)
        val database = MockDatabase(TestUtils.indexName, events)
        val webService = MockWebService()
        val uploader = IntegrationEventUploader(events, webService, database)
        val insights = Insights(TestUtils.indexName, uploader, database, webService)
        insights.minBatchSize = 1

        webService.code = 200 // Given a working web service
        insights.track(Event.Click(firstEvent.params))
        webService.code = -1 // Given a web service that errors
        insights.track(Event.Conversion(secondEvent.params))
        webService.code = 400 // Given a working web service returning an HTTP error
        insights.track(eventView) // When tracking an event

        webService.code = -1 // Given a web service that errors
        insights.userToken = TestUtils.eventClick.userToken // Given an userToken

        // When adding events without explicitly-provided userToken
        insights.search.click(firstEvent.eventName, firstEvent.indexName, firstEvent.timestamp, firstEvent.queryId!!, firstEvent.objectIDs)
        insights.personalization.click(firstEvent.eventName, firstEvent.indexName, firstEvent.timestamp, firstEvent.queryId, firstEvent.objectIDs)
        insights.personalization.conversion(secondEvent.eventName, secondEvent.indexName, secondEvent.timestamp, secondEvent.queryId, secondEvent.objectIDs)
        webService.code = 200 // Given a working web service
        insights.personalization.view(thirdEvent)
    }

    inner class IntegrationEventUploader internal constructor(
        private val events: MutableList<Event>,
        private val webService: MockWebService,
        private val database: MockDatabase
    ) : AssertingEventUploader(events, webService, database) {
        override fun startOneTimeUpload() {
            when (count) {
                0 -> assertEquals(listOf(firstEvent), database.read()) // expect added first
                1 -> assertEquals(listOf(secondEvent), database.read()) // expect flush then added second
                2 -> assertEquals(listOf(secondEvent, eventView), database.read()) // expect added third

                3 -> assertEquals(listOf(firstEvent), database.read()) // expect flush then added first
                4 -> assertEquals(listOf(firstEvent, firstEvent), database.read()) // expect added first
                5 -> assertEquals(listOf(firstEvent, firstEvent, secondEvent), database.read()) // expect added second
                6 -> assertEquals(listOf(firstEvent, firstEvent, secondEvent, thirdEvent), database.read()) // expect added third

            }
            webService.uploadEvents(database, TestUtils.indexName)
            when (count) {
                0 -> assert(database.read().isEmpty()) // expect flushed first
                1 -> assertEquals(listOf(secondEvent), database.read()) // expect kept second
                2 -> assert(database.read().isEmpty()) // expect flushed events

                3 -> assertEquals(listOf(firstEvent), database.read()) // expect kept first
                4 -> assertEquals(listOf(firstEvent, firstEvent), database.read()) // expect kept first2
                5 -> assertEquals(listOf(firstEvent, firstEvent, secondEvent), database.read()) // expect kept second
                6 -> assert(database.read().isEmpty()) // expect flushed events
            }
            count++
        }
    }

    abstract inner class AssertingEventUploader internal constructor(
        private val events: MutableList<Event>,
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
