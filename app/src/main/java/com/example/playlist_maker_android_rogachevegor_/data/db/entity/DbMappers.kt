package com.example.playlist_maker_android_rogachevegor_.data.db.entity

import com.example.playlist_maker_android_rogachevegor_.domain.models.Playlist
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track

fun PlaylistWithTracks.toPlaylist(): Playlist {
    return Playlist(
        id = playlist.id,
        name = playlist.name,
        description = playlist.description,
        coverImageUri = playlist.coverImageUri,
        tracks = tracks.map { track ->
            track.toTrack().copy(playlistId = playlist.id)
        }
    )
}

fun TrackEntity.toTrack(): Track {
    return Track(
        id = id,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        favorite = favorite,
        playlistId = 0
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        favorite = favorite,
        playlistId = playlistId
    )
}
