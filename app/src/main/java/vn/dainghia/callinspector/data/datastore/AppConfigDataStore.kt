package vn.dainghia.callinspector.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfigDataStore @Inject constructor(@ApplicationContext context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_config")
    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun getToken(): String? = dataStore.data.firstOrNull()?.get(KEY_TOKEN)

    suspend fun setToken(token: String) {
        dataStore.edit { mutablePreferences -> mutablePreferences[KEY_TOKEN] = token }
    }

    suspend fun getCountryCode(): String? = dataStore.data.firstOrNull()?.get(KEY_COUNTRY_CODE)

    suspend fun setCountryCode(countryCode: String) {
        dataStore.edit { mutablePreferences -> mutablePreferences[KEY_COUNTRY_CODE] = countryCode }
    }

    suspend fun getShouldShowCallerInfoOverlay(): Boolean? =
        dataStore.data.firstOrNull()?.get(KEY_SHOULD_SHOW_CALLER_INFO_OVERLAY)

    suspend fun setShouldShowCallerInfoOverlay(shouldShow: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_SHOULD_SHOW_CALLER_INFO_OVERLAY] = shouldShow
        }
    }

    companion object {
        private val KEY_TOKEN: Preferences.Key<String> = stringPreferencesKey("token")
        private val KEY_COUNTRY_CODE: Preferences.Key<String> = stringPreferencesKey("country_code")
        private val KEY_SHOULD_SHOW_CALLER_INFO_OVERLAY: Preferences.Key<Boolean> =
            booleanPreferencesKey("should_show_caller_info_overlay")
    }
}