package fr.laforge.benoist.financialmanager

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import fr.laforge.benoist.financialmanager.views.db.ImportDbViewModel
import fr.laforge.benoist.financialmanager.views.login.LoginViewModel
import fr.laforge.benoist.financialmanager.views.transaction.add.AddTransactionViewModel
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.repository.implementations.room.AndroidFinancialRepository
import fr.laforge.benoist.repository.implementations.room.database.AppDatabase

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddTransactionViewModel()
        }

        initializer {
            ImportDbViewModel()
        }

        initializer {
            LoginViewModel()
        }

        initializer {
            TransactionDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                financialRepository = financialManagerApplication().container.financialRepository,
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [GeneratorApp].
 */
fun CreationExtras.financialManagerApplication(): FinancialManagerApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FinancialManagerApp)
