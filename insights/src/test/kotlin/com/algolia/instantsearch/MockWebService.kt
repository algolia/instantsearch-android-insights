package com.algolia.instantsearch

import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.webservice.WebService


internal class MockWebService : WebService {

    var code: Int = 200

    override fun sendEvent(event: Event): WebService.Response {
        return WebService.Response(code = code, errorMessage = null)
    }
}
