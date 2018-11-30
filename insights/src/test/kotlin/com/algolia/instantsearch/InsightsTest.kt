package com.algolia.instantsearch

import com.algolia.instantsearch.TestUtils.eventClick
import com.algolia.instantsearch.TestUtils.eventConversion
import com.algolia.instantsearch.TestUtils.eventView
import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.ConverterEventToString
import com.algolia.instantsearch.insights.converter.ConverterParameterToString
import com.algolia.instantsearch.insights.converter.ConverterStringToEvent
import com.algolia.instantsearch.insights.converter.IndexNameKey
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.webservice.WebService
import com.algolia.instantsearch.insights.webservice.uploadEvents
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
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
        assertEquals(responseOK, TestUtils.webService.send(listOf(firstEvent)))
        // given an event built with typed constructor
        assertEquals(responseOK, TestUtils.webService.send(listOf(eventClick)))
    }

    @Test
    fun testViewEvent() {
        // given an event built raw
        assertEquals(responseOK, TestUtils.webService.send(listOf(thirdEvent)))
        // given an event built with typed constructor
        assertEquals(responseOK, TestUtils.webService.send(listOf(eventView)))
    }

    @Test
    fun testConversionEvent() {
        // given an event built raw
        assertEquals(responseOK, TestUtils.webService.send(listOf(secondEvent)))
        // given an event built with typed constructor
        assertEquals(responseOK, TestUtils.webService.send(listOf(eventConversion)))
    }

    /**
     * Tests the integration of events, WebService and Database.
     */
    @Test
    fun testIntegration() {
        val events = mutableListOf(firstEvent, secondEvent, thirdEvent)
        val database = MockDatabase(TestUtils.indexName, events)
        val webService = MockWebService()
        val uploader = AssertingEventUploader(events, webService, database)
        val insights = Insights(TestUtils.indexName, uploader, database, webService)

        webService.code = 200 // Given a working web service
        insights.track(Event.Click(firstEvent.params))
        webService.code = -1 // Given a web service that errors
        insights.track(Event.Conversion(secondEvent.params))
        webService.code = 400 // Given a working web service returning an HTTP error
        insights.track(eventView)

        webService.code = -1 // Given a web service that errors
        insights.search.click(firstEvent.params)
        insights.personalization.click(firstEvent.params)
        insights.personalization.conversion(secondEvent.params)
        webService.code = 200 // Given a working web service
        insights.personalization.view(thirdEvent.params)
    }

    inner class AssertingEventUploader internal constructor(
        private val events: MutableList<Event>,
        private val webService: MockWebService,
        private val database: MockDatabase
    ) : EventUploader {

        private var count: Int = 0

        override fun startPeriodicUpload() {
            assertEquals(events, database.read())
            webService.uploadEvents(database, TestUtils.indexName)
            assert(database.read().isEmpty())
        }

        override fun startOneTimeUpload() {
            when (count) {
                0 -> assertEquals(listOf(firstEvent), database.read()) // expect added first
                1 -> assertEquals(listOf(secondEvent), database.read()) // expect flush then added second
                2 -> assertEquals(listOf(secondEvent, eventView), database.read()) // expect added third

                3 -> assertEquals(listOf(firstEvent), database.read()) // expect flush then added first
                4 -> assertEquals(listOf(firstEvent, firstEvent), database.read()) // expect added first
                5 -> assertEquals(listOf(firstEvent, firstEvent, secondEvent), database.read()) // expect added second
                6 -> assertEquals(listOf(firstEvent, firstEvent, secondEvent, eventView), database.read()) // expect added third

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
}
