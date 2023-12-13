package fr.laforge.benoist.financialmanager.views.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.util.exportToCsvFormat
import fr.laforge.benoist.financialmanager.util.getFirstDayOfMonth
import fr.laforge.benoist.financialmanager.util.getLastDayOfMonth
import fr.laforge.benoist.financialmanager.util.sum
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime


class HomeScreenViewModel : ViewModel(), KoinComponent {
    private val repository: FinancialRepository by inject()

    val availableAmount : Flow<Float> = repository.getAllInDateRange(
        startDate = LocalDateTime.now().getFirstDayOfMonth(),
        endDate = LocalDateTime.now().getLastDayOfMonth()
    ).map {
        it.sum()
    }

    val periodicAmount: Flow<Float> = repository.getAllPeriodicTransactions().map{
        it.sum()
    }

    val allTransactions : Flow<List<Transaction>> = repository.getAllInDateRange(
        startDate = LocalDateTime.now().getFirstDayOfMonth(),
        endDate = LocalDateTime.now().getLastDayOfMonth()
    )

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            withContext(Dispatchers.IO + Job()) {
                repository.deleteTransaction(transaction = transaction)
            }
        }
    }

    fun saveDb(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
