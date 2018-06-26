package com.algolia.instantsearch.insights.database

import android.content.Context
import android.content.SharedPreferences


internal var SharedPreferences.serializedEvents by SharedPreferencesDelegate.StringSet(setOf())
internal var SharedPreferences.workerId by SharedPreferencesDelegate.String()

internal fun Context.sharedPreferences(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getSharedPreferences(name, mode)
}
