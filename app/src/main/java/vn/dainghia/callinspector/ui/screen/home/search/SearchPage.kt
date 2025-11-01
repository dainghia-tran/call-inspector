package vn.dainghia.callinspector.ui.screen.home.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import vn.dainghia.callinspector.ui.screen.home.search.composable.PhoneNumberSearchBar
import vn.dainghia.callinspector.util.applyIf

@Composable
fun SearchPage(modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isSearchBarFocused = interactionSource.collectIsFocusedAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(interactionSource = null, indication = null, onClick = {
                focusManager.clearFocus()
            })
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .applyIf(isSearchBarFocused) { fillMaxSize() }) {
                PhoneNumberSearchBar(
                    interactionSource = interactionSource,
                    isFocused = isSearchBarFocused,
                    onSearch = { },
                    modifier = Modifier.applyIf(isSearchBarFocused) { fillMaxWidth() }
                )
            }
        }
    }
}