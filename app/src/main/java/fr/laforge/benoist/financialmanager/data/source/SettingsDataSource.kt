package fr.laforge.benoist.financialmanager.data.source

import kotlinx.coroutines.flow.Flow

/**
 * A data source that provides access to the preferences of the app, in raw data format.
 */
interface SettingsDataSource {
    /**
     * Sets a float value in the data source.
     *
     * @param key The key of the value to set.
     * @param value The value to set.
     */
    suspend fun setFloat(key: String, value: Float)

    /**
     * Gets a float value from the data source.
     *
     * @param key The key of the value to get.
     * @return A flow of the value.
     */
    fun getFloat(key: String): Flow<Float>
}
