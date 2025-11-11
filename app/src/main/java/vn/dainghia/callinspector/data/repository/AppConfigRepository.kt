package vn.dainghia.callinspector.data.repository

import vn.dainghia.callinspector.data.datastore.AppConfigDataStore
import java.util.Locale
import javax.inject.Inject

class AppConfigRepository @Inject constructor(private val dataStore: AppConfigDataStore) {

    /**
     * Returns the stored token, return empty string if token was not set
     */
    suspend fun getToken(): String = dataStore.getToken() ?: ""

    suspend fun setToken(token: String): Unit = dataStore.setToken(token)

    /**
     * Returns the configured country code, return the default country code if was not set
     */
    suspend fun getCountryCode(): String {
        val configuredCountryCode = dataStore.getCountryCode()
        if (configuredCountryCode != null) {
            return configuredCountryCode
        }
        return Locale.getDefault().country
    }

    suspend fun setCountryCode(countryCode: String): Unit = dataStore.setCountryCode(countryCode)

    suspend fun getShouldShowCallerInfoOverlay(): Boolean =
        dataStore.getShouldShowCallerInfoOverlay() ?: false

    suspend fun setShouldShowCallerInfoOverlay(shouldShow: Boolean): Unit =
        dataStore.setShouldShowCallerInfoOverlay(shouldShow)
}