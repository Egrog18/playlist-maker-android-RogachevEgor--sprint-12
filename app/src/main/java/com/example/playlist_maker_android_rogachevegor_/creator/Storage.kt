package com.example.playlist_maker_android_rogachevegor_.creator

import com.example.playlist_maker_android_rogachevegor_.data.dto.TrackDto

class Storage {
    private val listTracks = listOf(
        TrackDto(
            trackName = "Yesterday (Remastered 2009)",
            artistName = "The Beatles",
            trackTimeMillis = 175000L
        ),
        TrackDto(
            trackName = "Here Comes The Sun (Remastered 2009)",
            artistName = "The Beatles",
            trackTimeMillis = 241000L
        ),
        TrackDto(
            trackName = "No Reply",
            artistName = "The Beatles",
            trackTimeMillis = 132000L
        ),
        TrackDto(
            trackName = "Let It Be",
            artistName = "The Beatles",
            trackTimeMillis = 243000L
        ),
        TrackDto(
            trackName = "Girl",
            artistName = "The Beatles",
            trackTimeMillis = 151000L
        ),
        TrackDto(
            trackName = "Michelle",
            artistName = "The Beatles",
            trackTimeMillis = 161000L
        ),
        TrackDto(
            trackName = "Eleanor Rigby",
            artistName = "The Beatles",
            trackTimeMillis = 126000L
        ),
        TrackDto(
            trackName = "Come Together",
            artistName = "The Beatles",
            trackTimeMillis = 259000L
        ),
        TrackDto(
            trackName = "Shape of You",
            artistName = "Ed Sheeran",
            trackTimeMillis = 233000L
        ),
        TrackDto(
            trackName = "Bohemian Rhapsody",
            artistName = "Queen",
            trackTimeMillis = 355000L
        )
    )

    fun search(request: String): List<TrackDto> {
        val query = request.trim().lowercase()

        return listTracks.filter {
            it.trackName.orEmpty().lowercase().contains(query) ||
                it.artistName.orEmpty().lowercase().contains(query)
        }
    }
}
