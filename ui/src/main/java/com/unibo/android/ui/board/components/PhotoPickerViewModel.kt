package com.unibo.android.ui.board.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.android.domain.di.UseCasesProvider
import com.unibo.android.domain.models.PhotoModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PhotoPickerState(
    val query: String = "",
    val photos: List<PhotoModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

class PhotoPickerViewModel : ViewModel() {

    private val _state = MutableStateFlow(PhotoPickerState())
    val state: StateFlow<PhotoPickerState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)
    }

    fun search(immediate: Boolean = false) {
        searchJob?.cancel()

        val query = _state.value.query
        if (query.isBlank()) {
            _state.value = _state.value.copy(photos = emptyList(), error = null, hasSearched = false)
            return
        }

        searchJob = viewModelScope.launch {
            if (!immediate) delay(400)   // debounce

            _state.value = _state.value.copy(isLoading = true, error = null)

            UseCasesProvider.searchPhotosUseCase(query)
                .onSuccess { photos ->
                    _state.value = _state.value.copy(
                        photos = photos,
                        isLoading = false,
                        error = null,
                        hasSearched = true
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        photos = emptyList(),
                        isLoading = false,
                        error = e.message ?: "Errore sconosciuto",
                        hasSearched = true
                    )
                }
        }
    }

    fun initWithQuery(initialQuery: String) {
        if (_state.value.hasSearched || initialQuery.isBlank()) return
        _state.value = _state.value.copy(query = initialQuery)
        search(immediate = true)
    }

    fun retry() = search(immediate = true)
}