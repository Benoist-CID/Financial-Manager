package fr.laforge.benoist.financialmanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.controller.PreferencesController
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {
    private val preferencesController: PreferencesController by inject()

    val savingsTarget = preferencesController.getSavingTarget()

    fun setSavingsTarget(newVal: Float) {
        viewModelScope.launch {
            preferencesController.setSavingsTarget(value = newVal)
        }
    }
}
