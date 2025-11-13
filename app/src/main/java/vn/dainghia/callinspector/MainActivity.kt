package vn.dainghia.callinspector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import vn.dainghia.callinspector.ui.screen.home.HomeScreen
import vn.dainghia.callinspector.ui.screen.home.settings.SettingsViewModel
import vn.dainghia.callinspector.ui.theme.CallInspectorTheme
import vn.dainghia.callinspector.util.CallerInfoOverlayPermissionHelper

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndUpdateConfig()

        setContent {
            CallInspectorTheme {
                HomeScreen()
            }
        }
    }

    private fun checkAndUpdateConfig() {
        val isEligible = CallerInfoOverlayPermissionHelper.isEligible(this)
        if (!isEligible) {
            settingsViewModel.updateShouldShowCallerInfoOverlay(false)
        }
    }
}