package fr.laforge.benoist.financialmanager.domain.repository

import fr.laforge.benoist.financialmanager.domain.model.sync.SyncSettings
import kotlinx.coroutines.flow.Flow

/**
 * Domain port for persisting and observing [SyncSettings].
 *
 * Intentionally separate from [PreferencesRepository] because sync settings are
 * expected to grow independently (e.g. per-account tolerances, date-window overrides).
 * Merging them into a general preferences store would couple unrelated concerns and
 * complicate future migration to a dedicated settings table.
 */
interface SyncSettingsRepository {

    /**
     * Returns a cold [Flow] that emits the current [SyncSettings] and re-emits
     * whenever the settings are updated via [save].
     *
     * @return A [Flow] of [SyncSettings]. Never completes unless the underlying
     *   data source is closed.
     */
    fun get(): Flow<SyncSettings>

    /**
     * Persists the given [settings], replacing all previously stored values.
     *
     * @param settings The new sync settings to store.
     */
    suspend fun save(settings: SyncSettings)
}
