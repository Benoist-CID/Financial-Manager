package fr.laforge.benoist.financialmanager.presentation.ui.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.util.exportToCsvFormat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val repository: FinancialRepository,
) : ViewModel() {
    val savingsTarget = preferencesRepository.getSavingTarget()

    fun setSavingsTarget(newVal: Float) {
        viewModelScope.launch {
            preferencesRepository.setSavingsTarget(value = newVal)
        }
    }

    /**
     * Saves the current database to a CSV file, and shares it with an intent.
     *
     * @param context The context of the activity.
     * @param dispatcher The dispatcher to use for the coroutine.
     */
    fun saveDb(
        context: Context,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        viewModelScope.launch {
            withContext(dispatcher) {
                repository.getAll().first { transactions ->
                    val sb = StringBuilder()
                    transactions.forEach {
                        sb.append(it.exportToCsvFormat() + "\n")
                    }

                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    // type of the content to be shared
                    sharingIntent.type = "text/plain"
                    // Body of the content
                    val shareBody = sb.toString()
                    // subject of the content. you can share anything
                    val shareSubject = "DB snapshot ${LocalDateTime.now()}"
                    // passing body of the content
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

                    // passing subject of the content
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    context.startActivity(Intent.createChooser(sharingIntent, "Share using"))

                    true
                }
            }
        }
    }
}
