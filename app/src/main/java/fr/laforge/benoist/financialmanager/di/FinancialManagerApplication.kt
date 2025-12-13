package fr.laforge.benoist.financialmanager.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.laforge.benoist.financialmanager.domain.usecase.CheckIfTransactionIsPeriodicUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractorImpl
import fr.laforge.benoist.financialmanager.domain.usecase.UpdateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.infrastructure.FinancialManagerApp
import fr.laforge.benoist.financialmanager.presentation.ui.db.ImportDbViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.login.LoginViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.add.AddTransactionViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.update.UpdateTransactionViewModel


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

        initializer {
            UpdateTransactionViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                financialRepository = financialManagerApplication().container.financialRepository,
                transactionInteractor = TransactionInteractorImpl(
                    checkIfTransactionIsPeriodicUseCase = CheckIfTransactionIsPeriodicUseCaseImpl(),
                    deleteTransactionUseCase = DeleteTransactionUseCaseImpl(
                        financialManagerApplication().container.financialRepository
                    ),
                    updateTransactionUseCase = UpdateTransactionUseCaseImpl(
                        financialRepository = financialManagerApplication().container.financialRepository
                    )
                ),
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
