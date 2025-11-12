package vn.dainghia.callinspector.ui.screen.home.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.dainghia.callinspector.data.model.TrueCallerResponse
import vn.dainghia.callinspector.ui.composable.CallerInfoCard
import vn.dainghia.callinspector.ui.screen.home.search.composable.PhoneNumberSearchBar
import vn.dainghia.callinspector.util.applyIf

@Composable
fun SearchPage(modifier: Modifier = Modifier, viewModel: SearchViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var isSearchBarFocused = interactionSource.collectIsFocusedAsState().value
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(interactionSource = null, indication = null, onClick = {
                focusManager.clearFocus()
            })
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .applyIf(isSearchBarFocused) { fillMaxSize() },
            ) {
                PhoneNumberSearchBar(
                    interactionSource = interactionSource,
                    isFocused = isSearchBarFocused,
                    onSearch = {
                        viewModel.search(it)
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .applyIf(!isSearchBarFocused && uiState != SearchUiState.Initial) {
                        weight(1f)
                    },
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is SearchUiState.Initial -> isSearchBarFocused = false
                    is SearchUiState.Loading -> LoadingState()
                    is SearchUiState.Success -> SuccessState((uiState).response)
                    is SearchUiState.Error -> ErrorState(uiState.errorMessage)
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    CircularProgressIndicator()
}

@Composable
private fun SuccessState(
    trueCallerResponse: TrueCallerResponse,
    modifier: Modifier = Modifier
) {
    CallerInfoCard(trueCallerResponse, modifier = modifier)
}

@Composable
private fun ErrorState(errorMessage: String) {
    Text(errorMessage)
}