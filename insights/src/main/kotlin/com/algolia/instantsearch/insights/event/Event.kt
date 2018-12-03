package com.algolia.instantsearch.insights.event


sealed class Event constructor(val params: Map<String, Any>) {

    companion object {
        //TODO: as Enum
        internal const val EventTypeKey = "eventType"
        internal const val EventNameKey = "eventName"
        internal const val IndexNameKey = "index"
        internal const val UserTokenKey = "userToken"
        internal const val TimestampKey = "timestamp"
        internal const val QueryIdKey = "queryId"
        internal const val ObjectIDsKey = "objectIDs"
        internal const val PositionsKey = "positions"
    }

    data class View constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null
    ) : Event(mutableMapOf<String, Any>().also { map ->
        map[EventTypeKey] = "view" //TODO: Cleaner eventType passing
        map[EventNameKey] = eventName
        map[IndexNameKey] = indexName
        map[UserTokenKey] = userToken
        map[TimestampKey] = timestamp
        queryId?.let { map[QueryIdKey] = it }
        objectIDs?.let { map[ObjectIDsKey] = it }
    }) {
        internal constructor(params: Map<String, Any>) : this(
            params[EventNameKey] as String,
            params[IndexNameKey] as String,
            params[UserTokenKey] as String,
            params[TimestampKey] as Long,
            params[QueryIdKey] as String?,
            params[ObjectIDsKey] as List<String>?) //TODO: Can I avoid uncheched cast?
    }

    data class Click constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null,
        val positions: List<Int>? = null
    ) : Event(mutableMapOf<String, Any>().also { map ->
        map[EventTypeKey] = "click"
        map[EventNameKey] = eventName
        map[IndexNameKey] = indexName
        map[UserTokenKey] = userToken
        map[TimestampKey] = timestamp
        queryId?.let { map[QueryIdKey] = it }
        objectIDs?.let { map[ObjectIDsKey] = it }
        positions?.let { map[PositionsKey] = it }
    }) {
        internal constructor(params: Map<String, Any>) : this(
            params[EventNameKey] as String,
            params[IndexNameKey] as String,
            params[UserTokenKey] as String,
            params[TimestampKey] as Long,
            params[QueryIdKey] as String?,
            params[ObjectIDsKey] as List<String>?)
    }

    data class Conversion constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null
    ) : Event(mutableMapOf<String, Any>().also { map ->
        map[EventTypeKey] = "conversion"
        map[EventNameKey] = eventName
        map[IndexNameKey] = indexName
        map[UserTokenKey] = userToken
        map[TimestampKey] = timestamp
        queryId?.let { map[QueryIdKey] = it }
        objectIDs?.let { map[ObjectIDsKey] = it }
    }) {
        internal constructor(params: Map<String, Any>) : this(
            params[EventNameKey] as String,
            params[IndexNameKey] as String,
            params[UserTokenKey] as String,
            params[TimestampKey] as Long,
            params[QueryIdKey] as String?,
            params[ObjectIDsKey] as List<String>?)
    }
}
