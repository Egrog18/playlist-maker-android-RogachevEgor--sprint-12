package com.example.playlist_maker_android_rogachevegor_.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["trackName", "artistName"], unique = true),
        Index(value = ["playlistId"])
    ]
)
data class TrackEntity(
    @PrimaryKey
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String?,
    val favorite: Boolean,
    val playlistId: Long
)
