package vn.dainghia.callinspector.ui.screen.home.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import vn.dainghia.callinspector.data.repository.AppConfigRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel() {

    var countryCode by mutableStateOf("")
        private set
    var accessToken by mutableStateOf("")
        private set
    var shouldShowCallerInfoOverlay by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            countryCode = appConfigRepository.getCountryCode()
            accessToken = appConfigRepository.getToken()
            shouldShowCallerInfoOverlay = appConfigRepository.getShouldShowCallerInfoOverlay()
        }
    }

    fun updateCountryCode(newCountryCode: String) {
        viewModelScope.launch {
            appConfigRepository.setCountryCode(newCountryCode)
            countryCode = newCountryCode
        }
    }

    fun updateAccessToken(newAccessToken: String) {
        viewModelScope.launch {
            appConfigRepository.setToken(newAccessToken)
            accessToken = newAccessToken
        }
    }

    fun updateShouldShowCallerInfoOverlay(shouldShow: Boolean) {
        viewModelScope.launch {
            appConfigRepository.setShouldShowCallerInfoOverlay(shouldShow)
            shouldShowCallerInfoOverlay = shouldShow
        }
    }
}