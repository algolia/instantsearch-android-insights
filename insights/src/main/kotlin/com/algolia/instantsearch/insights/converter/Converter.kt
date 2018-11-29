package com.algolia.instantsearch.insights.converter


internal interface Converter<in I, out O> {

    fun convert(input: I): O
    fun convert(inputs: List<I>): List<O> = inputs.map(::convert)
}

internal const val EventTypeKey = "eventType"
internal const val IndexNameKey = "index"