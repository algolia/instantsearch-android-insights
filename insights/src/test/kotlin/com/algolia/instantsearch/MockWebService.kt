package com.algolia.instantsearch

import com.algolia.instantsearch.insights.event.EventInternal
import com.algolia.instantsearch.insights.webservice.WebService


internal class MockWebService : WebService {

    var code: Int = 200

    override fun send(vararg event: EventInternal): WebService.Response {
        return WebService.Response(code = code, errorMessage = null)
    }
}
