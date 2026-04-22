package fr.laforge.benoist.financialmanager.infrastructure.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncSettings
import fr.laforge.benoist.financialmanager.domain.repository.SyncSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.syncSettingsDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "sync_settings")

/**
 * [SyncSettingsRepository] implementation backed by Jetpack DataStore (Preferences).
 *
 * Intentionally uses a **separate** DataStore file (`sync_settings`) rather than
 * reusing the general `settings` store. This avoids key collisions and allows the
 * two stores to evolve (e.g. be migrated to proto DataStore) independently.
 *
 * @param context Android [Context] used to access the DataStore instance.
 *
 * @note If new settings fields are added to [SyncSettings], add a new
 *   [androidx.datastore.preferences.core.Preferences.Key] constant in [Keys] and map it
 *   in both [get] and [save]. The default value in [get] should always match the default
 *   declared on the [SyncSettings] data class.
 */
class DataStoreSyncSettingsRepository(private val context: Context) : SyncSettingsRepository {

    /**
     * Returns a [Flow] of [SyncSettings] reflecting the currently persisted values.
     * Unknown (missing) keys fall back to [SyncSettings] defaults.
     */
    override fun get(): Flow<SyncSettings> = context.syncSettingsDataStore.data.map { prefs ->
        SyncSettings(
            recurringAmountTolerancePercent = prefs[Keys.TOLERANCE_PERCENT]
                ?: SyncSettings().recurringAmountTolerancePercent,
        )
    }

    /**
     * Persists [settings] to DataStore, replacing all previously stored values.
     *
     * @param settings The new sync settings to store.
     */
    override suspend fun save(settings: SyncSettings) {
        context.syncSettingsDataStore.edit { prefs ->
            prefs[Keys.TOLERANCE_PERCENT] = settings.recurringAmountTolerancePercent
        }
    }

    private object Keys {
        val TOLERANCE_PERCENT = floatPreferencesKey("recurring_amount_tolerance_percent")
    }
}
