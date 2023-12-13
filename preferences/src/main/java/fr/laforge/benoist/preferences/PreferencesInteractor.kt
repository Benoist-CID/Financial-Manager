package fr.laforge.benoist.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesInteractor {
    suspend fun setFloat(key: String, value: Float)
    fun getFloat(key: String): Flow<Float>
}
