package fr.laforge.benoist.financialmanager.views.home

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.credenceid.util.FileUtil
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
import java.time.LocalDateTime


class HomeScreenViewModel : ViewModel(), KoinComponent {
    private val repository: FinancialRepository by inject()
    private val context: Context by inject()

    val availableAmount : Flow<Float> = repository.getAllInDateRange(
        startDate = LocalDateTime.now().getFirstDayOfMonth(),
        endDate = LocalDateTime.now().getLastDayOfMonth()
    ).map {
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
                val fileUtil = FileUtil()
                repository.getAll().first { transactions ->
                    val sb = StringBuilder()
                    transactions.forEach {
                        sb.append(it.exportToCsvFormat() + "\n")
                    }
//                    fileUtil.write(File(Environment.getExternalStorageDirectory(), "Documents/Save.txt"), sb.toString())

                    val sharingIntent = Intent(Intent.ACTION_SEND)

                    // type of the content to be shared

                    // type of the content to be shared
                    sharingIntent.type = "text/plain"

                    // Body of the content

                    // Body of the content
                    val shareBody = sb.toString()

                    // subject of the content. you can share anything

                    // subject of the content. you can share anything
                    val shareSubject = "Your Subject Here"

                    // passing body of the content

                    // passing body of the content
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

                    // passing subject of the content

                    // passing subject of the content
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    context.startActivity(Intent.createChooser(sharingIntent, "Share using"))

                    true
                }
            }
        }
    }
}
