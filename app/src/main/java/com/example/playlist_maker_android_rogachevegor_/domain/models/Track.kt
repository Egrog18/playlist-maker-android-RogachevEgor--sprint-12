package com.example.playlist_maker_android_rogachevegor_.domain.models

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String? = null,
    val id: Long = generateTrackId(trackName = trackName, artistName = artistName),
    val favorite: Boolean = false,
    val playlistId: Long = 0
)

private fun generateTrackId(trackName: String, artistName: String): Long {
    val hash = "$trackName|$artistName".hashCode().toLong()
    return if (hash < 0) -hash else hash
}
