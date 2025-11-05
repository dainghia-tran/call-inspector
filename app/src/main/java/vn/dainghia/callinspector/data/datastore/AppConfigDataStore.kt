package vn.dainghia.callinspector.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AppConfigDataStore @Inject constructor(@ApplicationContext context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_config")
    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun getToken(): String? = dataStore.data.firstOrNull()?.get(KEY_TOKEN)

    suspend fun setToken(token: String) {
        dataStore.edit { mutablePreferences -> mutablePreferences[KEY_TOKEN] = token }
    }

    companion object {

        private val KEY_TOKEN: Preferences.Key<String> = stringPreferencesKey("token")
    }
}