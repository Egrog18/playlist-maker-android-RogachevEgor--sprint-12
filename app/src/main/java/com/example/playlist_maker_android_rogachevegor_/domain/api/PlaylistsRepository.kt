package com.example.playlist_maker_android_rogachevegor_.domain.api

import com.example.playlist_maker_android_rogachevegor_.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<Playlist?>

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addNewPlaylist(
        name: String,
        description: String,
        coverImageUri: String? = null
    )

    suspend fun deletePlaylistById(id: Long)
}
