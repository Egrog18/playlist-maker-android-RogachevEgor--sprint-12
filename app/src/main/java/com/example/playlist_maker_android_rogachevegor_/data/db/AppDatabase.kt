package com.example.playlist_maker_android_rogachevegor_.data.db

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlist_maker_android_rogachevegor_.data.db.dao.PlaylistDao
import com.example.playlist_maker_android_rogachevegor_.data.db.dao.TrackDao
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.PlaylistEntity
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.PlaylistTrackCrossRef
import com.example.playlist_maker_android_rogachevegor_.data.db.entity.TrackEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        TrackEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao

    abstract fun trackDao(): TrackDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE playlists ADD COLUMN coverImageUri TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS playlist_tracks (
                        playlistId INTEGER NOT NULL,
                        trackId INTEGER NOT NULL,
                        PRIMARY KEY(playlistId, trackId)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_playlist_tracks_playlistId ON playlist_tracks(playlistId)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_playlist_tracks_trackId ON playlist_tracks(trackId)"
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO playlist_tracks(playlistId, trackId)
                    SELECT playlistId, id FROM tracks
                    WHERE playlistId != 0
                    """.trimIndent()
                )
                db.execSQL("UPDATE tracks SET playlistId = 0 WHERE playlistId != 0")
            }
        }
    }
}
