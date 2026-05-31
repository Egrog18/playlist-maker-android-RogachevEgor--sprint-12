package com.example.playlist_maker_android_rogachevegor_.data.repository

import com.example.playlist_maker_android_rogachevegor_.data.db.AppDatabase
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.PlaylistEntity
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.toPlaylist
import com.example.playlist_maker_android_rogachevegor_.data.storage.PlaylistCoverStorage
import com.example.playlist_maker_android_rogachevegor_.domain.api.PlaylistsRepository
import com.example.playlist_maker_android_rogachevegor_.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    database: AppDatabase,
    private val playlistCoverStorage: PlaylistCoverStorage
) : PlaylistsRepository {
    private val dao = database.playlistDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return dao.getPlaylist(playlistId).map { playlistWithTracks ->
            playlistWithTracks?.toPlaylist()
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return dao.getAllPlaylists().map { playlists ->
            playlists.map { playlistWithTracks -> playlistWithTracks.toPlaylist() }
        }
    }

    override suspend fun addNewPlaylist(
        name: String,
        description: String,
        coverImageUri: String?
    ) {
        val localCoverImageUri = playlistCoverStorage.copyToLocalStorage(coverImageUri)

        dao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = localCoverImageUri
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        val coverImageUri = dao.getCoverImageUri(playlistId = id)
        dao.deletePlaylistById(playlistId = id)
        playlistCoverStorage.deleteCover(coverImageUri)
    }
}
