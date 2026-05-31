package com.example.playlist_maker_android_rogachevegor_.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_rogachevegor_.creator.Creator
import com.example.playlist_maker_android_rogachevegor_.domain.api.TracksRepository
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllTracksViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _allTracksScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val allTracksScreenState = _allTracksScreenState.asStateFlow()

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _allTracksScreenState.update { SearchState.Searching }
                val tracks = tracksRepository.searchTracks("")
                _allTracksScreenState.update { SearchState.Success(foundList = tracks) }
            } catch (e: IOException) {
                _allTracksScreenState.update { SearchState.Fail(e.message.orEmpty()) }
            }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AllTracksViewModel(Creator.getTracksRepository()) as T
                }
            }
    }
}
