package com.example.playlist_maker_android_rogachevegor_.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker_android_rogachevegor_.creator.Creator
import com.example.playlist_maker_android_rogachevegor_.domain.api.PlaylistsRepository
import com.example.playlist_maker_android_rogachevegor_.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistViewModel(
    playlistsRepository: PlaylistsRepository,
    playlistId: Long
) : ViewModel() {

    val playlist: Flow<Playlist?> = playlistsRepository.getPlaylist(playlistId)

    companion object {
        fun getViewModelFactory(playlistId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistViewModel(
                        playlistsRepository = Creator.getPlaylistsRepository(),
                        playlistId = playlistId
                    ) as T
                }
            }
    }
}
