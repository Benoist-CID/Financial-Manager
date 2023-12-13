package fr.laforge.benoist.financialmanager.controller

import kotlinx.coroutines.flow.Flow

interface PreferencesController {
    suspend fun setTarget(value: Float)
    fun getTarget(): Flow<Float>
}
