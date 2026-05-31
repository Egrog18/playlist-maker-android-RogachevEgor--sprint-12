package com.example.playlist_maker_android_rogachevegor_.domain.models

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,
    var tracks: List<Track>
)
