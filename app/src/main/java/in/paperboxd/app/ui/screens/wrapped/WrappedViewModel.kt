package `in`.paperboxd.app.ui.screens.wrapped

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.WrappedRepository
import `in`.paperboxd.app.domain.model.Wrapped
import `in`.paperboxd.app.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WrappedViewModel @Inject constructor(
    private val repository: WrappedRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Wrapped>>(UiState.Loading)
    val state: StateFlow<UiState<Wrapped>> = _state.asStateFlow()

    init {
        load()
    }

    /** [month] is YYYY-MM; null loads the month in progress. */
    fun load(month: String? = null) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.monthly(month)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure {
                    _state.value = UiState.Error(it.message ?: "Could not load your Wrapped.")
                }
        }
    }
}
