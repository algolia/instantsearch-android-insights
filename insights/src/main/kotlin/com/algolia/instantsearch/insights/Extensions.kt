package com.algolia.instantsearch.insights

import android.content.Context
import android.content.SharedPreferences


internal const val EventTypeKey = "type"

internal var SharedPreferences.events by SharedPreferencesDelegate.StringSet(setOf())
internal var SharedPreferences.workerId by SharedPreferencesDelegate.String()

internal fun Context.sharedPreferences(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getSharedPreferences(name, mode)
}
