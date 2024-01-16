package fr.laforge.benoist.financialmanager.controller

import fr.laforge.benoist.preferences.PreferencesInteractor

class PreferencesControllerImpl(private val preferencesInteractor: PreferencesInteractor) : PreferencesController {
    override suspend fun setSavingsTarget(value: Float) {
        preferencesInteractor.setFloat(key = TARGET_KEY, value = value)
    }

    override fun getSavingTarget() = preferencesInteractor.getFloat(TARGET_KEY)

    companion object {
        private const val TARGET_KEY = "target"
    }
}
