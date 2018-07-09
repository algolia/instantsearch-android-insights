package com.algolia.instantsearch

import com.algolia.instantsearch.insights.Insights
import com.algolia.instantsearch.insights.converter.ConverterEventToString
import com.algolia.instantsearch.insights.converter.ConverterStringToEvent
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventUploader
import com.algolia.instantsearch.insights.webservice.WebService
import com.algolia.instantsearch.insights.webservice.uploadEvents
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals


@RunWith(JUnit4::class)
class InsightsTest {

    @Test
    fun testConverterEvent() {
        val expected = Event.Click(TestUtils.eventParametersA)
        val string = ConverterEventToString.convert(expected)
        val event = ConverterStringToEvent.convert(string)

        assertEquals(expected, event)
    }

    @Test
    fun testClickEvent() {
        val response = WebService.Response(
            errorMessage = null,
            code = 200
        )
        val event = Event.Click(TestUtils.eventParametersA)
        assertEquals(response, TestUtils.webService.sendEvent(event))
    }

    @Test
    fun testViewEvent() {
        val response = WebService.Response(
            errorMessage = "",
            code = 404
        )
        val event = Event.View(TestUtils.eventParametersA)
        assertEquals(response.code, TestUtils.webService.sendEvent(event).code)
    }

    @Test
    fun testConversionEvent() {
        val response = WebService.Response(
            errorMessage = null,
            code = 200
        )
        val event = Event.Conversion(TestUtils.eventParametersA)
        assertEquals(response, TestUtils.webService.sendEvent(event))
    }

    @Test
    fun testIntegration() {
        val firstEvent = Event.Click(TestUtils.eventParametersA)
        val secondEvent = Event.Conversion(TestUtils.eventParametersB)
        val thirdEvent = Event.View(TestUtils.eventParametersC)
        val events = mutableListOf(firstEvent, secondEvent, thirdEvent)
        val database = MockDatabase(TestUtils.indexName, events)
        val webService = MockWebService()

        val uploader = object : EventUploader {

            var count: Int = 0

            override fun startPeriodicUpload() {
                assertEquals(events, database.read())
                webService.uploadEvents(database, TestUtils.indexName)
                assert(database.read().isEmpty())
            }

            override fun startOneTimeUpload() {
                when (count) {
                    0 -> assertEquals(listOf(firstEvent), database.read())
                    1 -> assertEquals(listOf(secondEvent), database.read())
                    2 -> assertEquals(listOf(secondEvent, thirdEvent), database.read())
                }
                webService.uploadEvents(database, TestUtils.indexName)
                when (count) {
                    0 -> assert(database.read().isEmpty())
                    1 -> assertEquals(listOf(secondEvent), database.read())
                    2 -> assert(database.read().isEmpty())
                }
                count++
            }
        }
        val insights = Insights(TestUtils.indexName, uploader, database, webService)

        webService.code = 200
        insights.click(firstEvent.params)
        webService.code = -1
        insights.conversion(secondEvent.params)
        webService.code = 400
        insights.view(thirdEvent.params)
    }
}
