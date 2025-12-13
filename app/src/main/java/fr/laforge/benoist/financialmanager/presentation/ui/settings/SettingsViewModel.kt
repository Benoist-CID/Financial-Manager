package fr.laforge.benoist.financialmanager.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val savingsTarget = preferencesRepository.getSavingTarget()

    fun setSavingsTarget(newVal: Float) {
        viewModelScope.launch {
            preferencesRepository.setSavingsTarget(value = newVal)
        }
    }
}
