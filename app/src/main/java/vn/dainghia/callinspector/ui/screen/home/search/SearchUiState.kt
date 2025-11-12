package vn.dainghia.callinspector.ui.screen.home.search

import vn.dainghia.callinspector.data.model.TrueCallerResponse

sealed class SearchUiState {

    object Initial : SearchUiState()

    object Loading : SearchUiState()

    data class Error(val errorMessage: String) : SearchUiState()

    data class Success(val response: TrueCallerResponse) : SearchUiState()
}