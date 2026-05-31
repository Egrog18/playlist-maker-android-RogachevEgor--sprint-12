package com.example.playlist_maker_android_rogachevegor_.creator

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.playlist_maker_android_rogachevegor_.data.db.AppDatabase
import com.example.playlist_maker_android_rogachevegor_.data.network.RetrofitNetworkClient
import com.example.playlist_maker_android_rogachevegor_.data.preferences.SearchHistoryPreferences
import com.example.playlist_maker_android_rogachevegor_.data.repository.PlaylistsRepositoryImpl
import com.example.playlist_maker_android_rogachevegor_.data.repository.SearchHistoryRepositoryImpl
import com.example.playlist_maker_android_rogachevegor_.data.repository.TracksRepositoryImpl
import com.example.playlist_maker_android_rogachevegor_.data.storage.PlaylistCoverStorage
import com.example.playlist_maker_android_rogachevegor_.domain.api.NetworkClient
import com.example.playlist_maker_android_rogachevegor_.domain.api.PlaylistsRepository
import com.example.playlist_maker_android_rogachevegor_.domain.api.SearchHistoryRepository
import com.example.playlist_maker_android_rogachevegor_.domain.api.TracksRepository

private val Context.searchHistoryDataStore by preferencesDataStore(name = "search_history")

object Creator {
    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        if (!::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }

    private val networkClient: NetworkClient by lazy {
        RetrofitNetworkClient()
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            requireApplicationContext(),
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3
            )
            .build()
    }

    private val searchHistoryPreferences: SearchHistoryPreferences by lazy {
        SearchHistoryPreferences(requireApplicationContext().searchHistoryDataStore)
    }

    private val playlistCoverStorage: PlaylistCoverStorage by lazy {
        PlaylistCoverStorage(requireApplicationContext())
    }

    private val tracksRepositoryInstance: TracksRepository by lazy {
        TracksRepositoryImpl(
            networkClient = networkClient,
            database = database
        )
    }

    private val playlistsRepositoryInstance: PlaylistsRepository by lazy {
        PlaylistsRepositoryImpl(
            database = database,
            playlistCoverStorage = playlistCoverStorage
        )
    }

    private val searchHistoryRepositoryInstance: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(preferences = searchHistoryPreferences)
    }

    fun getTracksRepository(): TracksRepository = tracksRepositoryInstance

    fun getPlaylistsRepository(): PlaylistsRepository = playlistsRepositoryInstance

    fun getSearchHistoryRepository(): SearchHistoryRepository = searchHistoryRepositoryInstance

    private fun requireApplicationContext(): Context {
        check(::applicationContext.isInitialized) {
            "Creator.initialize(context) must be called before requesting repositories"
        }
        return applicationContext
    }

    private const val DATABASE_NAME = "playlist_maker.db"
}
