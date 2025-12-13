package fr.laforge.benoist.financialmanager.infrastructure.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.laforge.benoist.financialmanager.data.source.SettingsDataSource
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSettingsDataSource(private val context: Context) : SettingsDataSource {
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
