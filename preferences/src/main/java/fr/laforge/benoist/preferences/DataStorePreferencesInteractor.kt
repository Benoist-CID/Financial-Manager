package fr.laforge.benoist.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStorePreferencesInteractor(private val context: Context) : PreferencesInteractor {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override suspend fun setFloat(key: String, value: Float) {
        context.dataStore.edit { settings ->
            settings[floatPreferencesKey(key)] = value
        }
    }

    override fun getFloat(key: String) = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[floatPreferencesKey(key)] ?: 0F
        }
}
