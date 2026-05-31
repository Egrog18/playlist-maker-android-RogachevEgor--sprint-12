package com.example.playlist_maker_android_rogachevegor_.data.repository

import com.example.playlist_maker_android_rogachevegor_.data.preferences.SearchHistoryPreferences
import com.example.playlist_maker_android_rogachevegor_.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val preferences: SearchHistoryPreferences
) : SearchHistoryRepository {
    override fun addEntry(word: String) {
        preferences.addEntry(word)
    }

    override suspend fun getEntries(): List<String> {
        return preferences.getEntries()
    }
}
