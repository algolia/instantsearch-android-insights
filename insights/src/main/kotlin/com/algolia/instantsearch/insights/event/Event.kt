package com.algolia.instantsearch.insights.event


sealed class Event(val params: Map<String, Any?>) {

    companion object {
        //TODO: as Enum
        internal const val EventTypeKey = "eventType"
        internal const val EventNameKey = "eventName"
        internal const val IndexNameKey = "index"
        internal const val UserTokenKey = "userToken"
        internal const val TimestampKey = "timestamp"
        internal const val QueryIdKey = "queryID"
        internal const val ObjectIDsKey = "objectIDs"
        internal const val PositionsKey = "positions"
    }

    data class Click constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null,
        val positions: List<Int>? = null
    ) : Event(
        mapOf(
            EventTypeKey to "click",
            EventNameKey to eventName,
            IndexNameKey to indexName,
            UserTokenKey to userToken,
            TimestampKey to timestamp,
            QueryIdKey to queryId,
            ObjectIDsKey to objectIDs,
            PositionsKey to positions
        )
    ) {
        internal constructor(params: Map<String, Any?>) : this(
            params[EventNameKey] as String,
            params[IndexNameKey] as String,
            params[UserTokenKey] as String,
            params[TimestampKey] as Long,
            params[QueryIdKey] as String?,
            params[ObjectIDsKey] as List<String>?,
            params[PositionsKey] as List<Int>?
        )
    }

    data class View constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null
    ) : Event(
        mapOf(
            EventTypeKey to "view", //TODO: Cleaner eventType passing
            EventNameKey to eventName,
            IndexNameKey to indexName,
            UserTokenKey to userToken,
            TimestampKey to timestamp,
            QueryIdKey to queryId,
            ObjectIDsKey to objectIDs
        )
    ) {

        internal constructor(params: Map<String, Any?>) : this(
            params[EventNameKey] as String,
            params[IndexNameKey] as String,
            params[UserTokenKey] as String,
            params[TimestampKey] as Long,
            params[QueryIdKey] as String?,
            params[ObjectIDsKey] as List<String>?
        )
    }

    data class Conversion constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null
    ) : Event(
        mapOf(
            EventTypeKey to "conversion",
            EventNameKey to eventName,
            IndexNameKey to indexName,
            UserTokenKey to userToken,
            TimestampKey to timestamp,
            QueryIdKey to queryId,
            ObjectIDsKey to objectIDs
        )
    ) {
        internal constructor(params: Map<String, Any?>) : this(
            params[EventNameKey] as String,
            params[IndexNameKey] as String,
            params[UserTokenKey] as String,
            params[TimestampKey] as Long,
            params[QueryIdKey] as String?,
            params[ObjectIDsKey] as List<String>?
        )
    }
}
