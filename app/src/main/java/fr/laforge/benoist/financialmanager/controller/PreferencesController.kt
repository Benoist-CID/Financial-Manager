package fr.laforge.benoist.financialmanager.controller

import kotlinx.coroutines.flow.Flow

interface PreferencesController {
    suspend fun setSavingsTarget(value: Float)
    fun getSavingTarget(): Flow<Float>
}
