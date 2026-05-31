package com.example.playlist_maker_android_rogachevegor_.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_rogachevegor_.creator.Creator
import com.example.playlist_maker_android_rogachevegor_.domain.api.SearchHistoryRepository
import com.example.playlist_maker_android_rogachevegor_.domain.api.TracksRepository
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()
    private val _historyEntries = MutableStateFlow<List<String>>(emptyList())
    val historyEntries = _historyEntries.asStateFlow()

    private var searchJob: Job? = null
    private var lastFailedExpression: String? = null

    init {
        refreshHistory()
    }

    fun search(whatSearch: String) {
        val expression = whatSearch.trim()
        if (expression.isEmpty()) {
            clearSearch()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.update { SearchState.Searching }
                searchHistoryRepository.addEntry(expression)
                _historyEntries.update { entries -> entries.withNewEntry(expression) }
                val tracks = tracksRepository.searchTracks(expression = expression)
                lastFailedExpression = null
                _searchScreenState.update { SearchState.Success(foundList = tracks) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                lastFailedExpression = expression
                _searchScreenState.update { SearchState.Fail(error = e.message.orEmpty()) }
            } catch (e: Exception) {
                lastFailedExpression = expression
                _searchScreenState.update { SearchState.Fail(error = e.message.orEmpty()) }
            }
        }
    }

    fun retryLastFailedSearch() {
        val expression = lastFailedExpression ?: return
        search(expression)
    }

    fun clearSearch() {
        searchJob?.cancel()
        lastFailedExpression = null
        _searchScreenState.update { SearchState.Initial }
        refreshHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val entries = searchHistoryRepository.getEntries()
            _historyEntries.update { entries }
        }
    }

    private fun List<String>.withNewEntry(entry: String): List<String> {
        return toMutableList()
            .apply {
                remove(entry)
                add(0, entry)
            }
            .take(MAX_HISTORY_ENTRIES)
    }

    companion object {
        private const val MAX_HISTORY_ENTRIES = 10

        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        tracksRepository = Creator.getTracksRepository(),
                        searchHistoryRepository = Creator.getSearchHistoryRepository()
                    ) as T
                }
            }
    }
}
