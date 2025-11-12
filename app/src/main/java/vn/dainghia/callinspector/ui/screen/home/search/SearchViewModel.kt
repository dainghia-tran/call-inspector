package vn.dainghia.callinspector.ui.screen.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.dainghia.callinspector.data.repository.TrueCallerRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trueCallerRepository: TrueCallerRepository
) : ViewModel() {

    private val mutableUiStateFlow: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.Initial)
    val uiStateFlow: StateFlow<SearchUiState> = mutableUiStateFlow.asStateFlow()

    fun search(phoneNumber: String) {
        viewModelScope.launch {
            mutableUiStateFlow.value = SearchUiState.Loading
            trueCallerRepository.searchCallerInfo(phoneNumber).fold(
                onSuccess = {
                    mutableUiStateFlow.value = SearchUiState.Success(it)
                },
                onFailure = {
                    mutableUiStateFlow.value = SearchUiState.Error(it.localizedMessage.orEmpty())
                }
            )
        }
    }
}