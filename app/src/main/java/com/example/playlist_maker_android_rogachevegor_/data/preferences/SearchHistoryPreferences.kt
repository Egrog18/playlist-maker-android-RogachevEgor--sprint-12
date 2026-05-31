package com.example.playlist_maker_android_rogachevegor_.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("search-history-preferences") + SupervisorJob()
    )
) {
    fun addEntry(word: String) {
        val trimmedWord = word.trim()
        if (trimmedWord.isEmpty()) {
            return
        }

        coroutineScope.launch {
            dataStore.edit { preferences ->
                val history = preferences[preferencesKey]
                    .orEmpty()
                    .toHistoryList()
                    .toMutableList()

                history.remove(trimmedWord)
                history.add(0, trimmedWord)

                preferences[preferencesKey] = history
                    .take(MAX_ENTRIES)
                    .joinToString(SEPARATOR)
            }
        }
    }

    suspend fun getEntries(): List<String> {
        return dataStore.data
            .first()[preferencesKey]
            .orEmpty()
            .toHistoryList()
    }

    private fun String.toHistoryList(): List<String> {
        return if (isEmpty()) {
            emptyList()
        } else {
            split(SEPARATOR).filter { entry -> entry.isNotBlank() }
        }
    }

    companion object {
        private const val MAX_ENTRIES = 10
        private const val SEPARATOR = ","
        private val preferencesKey = stringPreferencesKey("search_history")
    }
}
