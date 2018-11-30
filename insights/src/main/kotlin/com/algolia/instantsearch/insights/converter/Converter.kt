package com.algolia.instantsearch.insights.converter


internal interface Converter<in I, out O> {

    fun convert(input: I): O
    fun convert(inputs: List<I>): List<O> = inputs.map(::convert)
}

internal const val EventTypeKey = "eventType"
internal const val EventNameKey = "eventName"
internal const val IndexNameKey = "index"
internal const val UserTokenKey = "userToken"
internal const val TimestampKey = "timestamp"
internal const val QueryIdKey = "queryId"
internal const val ObjectIDsKey = "objectIDs"
internal const val PositionsKey = "positions"