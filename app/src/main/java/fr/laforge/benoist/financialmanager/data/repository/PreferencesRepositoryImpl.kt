package fr.laforge.benoist.financialmanager.data.repository

import fr.laforge.benoist.financialmanager.data.source.SettingsDataSource
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository

/**
 * A repository that provides access to the preferences of the app.
 */
class PreferencesRepositoryImpl(private val settingsDataSource: SettingsDataSource) :
    PreferencesRepository {
    override suspend fun setSavingsTarget(value: Float) = settingsDataSource.setFloat(key = TARGET_KEY, value = value)

    override fun getSavingTarget() = settingsDataSource.getFloat(TARGET_KEY)

    companion object {
        private const val TARGET_KEY = "target"
    }
}
