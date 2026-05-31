package com.example.playlist_maker_android_rogachevegor_.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.PlaylistEntity
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Transaction
    @Query("SELECT * FROM playlists ORDER BY id")
    fun getAllPlaylists(): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    fun getPlaylist(playlistId: Long): Flow<PlaylistWithTracks?>

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT coverImageUri FROM playlists WHERE id = :playlistId LIMIT 1")
    suspend fun getCoverImageUri(playlistId: Long): String?

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)
}
