package vn.dainghia.callinspector.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import vn.dainghia.callinspector.R
import vn.dainghia.callinspector.ui.screen.home.search.SearchPage
import vn.dainghia.callinspector.ui.screen.home.settings.SettingsPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var currentPageIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { Page.entries.size }

    LaunchedEffect(currentPageIndex) {
        pagerState.scrollToPage(currentPageIndex)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(Page.entries[currentPageIndex].titleResId)) }) },
        bottomBar = {
            NavigationBar {
                Page.entries.forEachIndexed { index, page ->
                    NavigationBarItem(
                        selected = currentPageIndex == index,
                        onClick = { currentPageIndex = index },
                        icon = { Icon(imageVector = page.icon, contentDescription = null) },
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                Page.Search.ordinal -> SearchPage()
                Page.Settings.ordinal -> SettingsPage()
            }
        }
    }
}

private enum class Page(@param:StringRes val titleResId: Int, val icon: ImageVector) {
    Search(R.string.search, Icons.Outlined.PersonSearch),
    Settings(R.string.settings, Icons.Outlined.Settings)
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}