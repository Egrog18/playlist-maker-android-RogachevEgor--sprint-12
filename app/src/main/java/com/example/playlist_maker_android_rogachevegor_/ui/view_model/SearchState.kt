package com.example.playlist_maker_android_rogachevegor_.ui.view_model

import com.example.playlist_maker_android_rogachevegor_.domain.models.Track

sealed class SearchState {
    data object Initial : SearchState()
    data object Searching : SearchState()
    data class Success(val foundList: List<Track>) : SearchState()
    data class Fail(val error: String) : SearchState()
}
