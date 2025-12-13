package fr.laforge.benoist.financialmanager.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * A repository that provides access to the preferences of the app.
 */
interface PreferencesRepository {
    suspend fun setSavingsTarget(value: Float)
    fun getSavingTarget(): Flow<Float>
}
