package com.example.playlist_maker_android_rogachevegor_.data.repository

import com.example.playlist_maker_android_rogachevegor_.data.db.AppDatabase
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.PlaylistTrackCrossRef
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.toEntity
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.toTrack
import com.example.playlist_maker_android_rogachevegor_.data.dto.TrackDto
import com.example.playlist_maker_android_rogachevegor_.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_rogachevegor_.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_rogachevegor_.domain.api.NetworkClient
import com.example.playlist_maker_android_rogachevegor_.domain.api.TracksRepository
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    database: AppDatabase
) : TracksRepository {
    private val dao = database.trackDao()

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return if (response.resultCode in HTTP_SUCCESS_CODES && response is TracksSearchResponse) {
            response.results.map { dto ->
                dto.toTrack()
            }.map { track ->
                val savedTrack = dao.getTrackByNameAndArtist(
                    trackName = track.trackName,
                    artistName = track.artistName
                ).firstOrNull()?.toTrack()
                if (savedTrack == null) {
                    dao.insertTrack(track.toEntity())
                    track
                } else {
                    val mergedTrack = savedTrack.copy(
                        trackTime = track.trackTime,
                        artworkUrl100 = track.artworkUrl100 ?: savedTrack.artworkUrl100
                    )
                    dao.insertTrack(mergedTrack.toEntity())
                    mergedTrack
                }
            }
        } else {
            throw IOException()
        }
    }

    private fun TrackDto.toTrack(): Track {
        val trackName = this.trackName.orEmpty().trim().ifEmpty { UNKNOWN_TRACK_NAME }
        val artistName = this.artistName.orEmpty().trim().ifEmpty { UNKNOWN_ARTIST_NAME }
        val safeTrackTimeMillis = this.trackTimeMillis?.coerceAtLeast(0L) ?: 0L
        val seconds = safeTrackTimeMillis / 1000
        val minutes = seconds / 60
        val trackTime = "%d:%02d".format(minutes, seconds - minutes * 60)
        val safeTrackId = this.trackId?.takeIf { it > 0 }
        val artworkUrl100 = this.artworkUrl100.orEmpty().trim().ifEmpty { null }

        return if (safeTrackId == null) {
            Track(
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                artworkUrl100 = artworkUrl100
            )
        } else {
            Track(
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                artworkUrl100 = artworkUrl100,
                id = safeTrackId
            )
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return dao.getTrackByNameAndArtist(
            trackName = track.trackName,
            artistName = track.artistName
        ).map { trackEntity -> trackEntity?.toTrack() }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getFavoriteTracks().map { tracks ->
            tracks.map { trackEntity -> trackEntity.toTrack() }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val savedTrack = dao.getTrackByNameAndArtist(
            trackName = track.trackName,
            artistName = track.artistName
        ).firstOrNull()?.toTrack()
        val updatedTrack = (savedTrack ?: track).copy(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100 ?: savedTrack?.artworkUrl100,
            playlistId = 0
        )
        dao.insertTrack(updatedTrack.toEntity())
        dao.insertPlaylistTrack(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = updatedTrack.id
            )
        )
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        val playlistId = track.playlistId.takeIf { it > 0 } ?: return
        val savedTrack = dao.getTrackByNameAndArtist(
            trackName = track.trackName,
            artistName = track.artistName
        ).firstOrNull()?.toTrack() ?: track
        dao.deleteTrackFromPlaylist(
            trackId = savedTrack.id,
            playlistId = playlistId
        )
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val savedTrack = dao.getTrackByNameAndArtist(
            trackName = track.trackName,
            artistName = track.artistName
        ).firstOrNull()?.toTrack() ?: track
        dao.insertTrack(savedTrack.copy(favorite = isFavorite).toEntity())
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        dao.deleteTracksByPlaylistId(playlistId)
    }

    companion object {
        private const val UNKNOWN_TRACK_NAME = "Unknown track"
        private const val UNKNOWN_ARTIST_NAME = "Unknown artist"
        private val HTTP_SUCCESS_CODES = 200..299
    }
}
