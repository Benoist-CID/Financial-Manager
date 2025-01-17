package fr.laforge.benoist.financialmanager

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.laforge.benoist.financialmanager.controller.PreferencesControllerImpl
import fr.laforge.benoist.financialmanager.domain.usecases.CheckIfTransactionIsPeriodicUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecases.UpdateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.interactors.TransactionInteractorImpl
import fr.laforge.benoist.financialmanager.ui.db.ImportDbViewModel
import fr.laforge.benoist.financialmanager.ui.home.HomeScreenViewModel
import fr.laforge.benoist.financialmanager.ui.login.LoginViewModel
import fr.laforge.benoist.financialmanager.ui.transaction.add.AddTransactionViewModel
import fr.laforge.benoist.financialmanager.ui.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.financialmanager.ui.transaction.update.UpdateTransactionViewModel
import fr.laforge.benoist.preferences.DataStorePreferencesInteractor


object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel(
                repository = financialManagerApplication().container.financialRepository,
                transactionInteractor = TransactionInteractorImpl(
                    checkIfTransactionIsPeriodicUseCase = CheckIfTransactionIsPeriodicUseCaseImpl(),
                    deleteTransactionUseCase = DeleteTransactionUseCaseImpl(
                        financialManagerApplication().container.financialRepository
                    ),
                    updateTransactionUseCase = UpdateTransactionUseCaseImpl(
                        financialRepository = financialManagerApplication().container.financialRepository
                    )
                ),
                preferencesController = PreferencesControllerImpl(
                    preferencesInteractor = DataStorePreferencesInteractor(
                        context = financialManagerApplication()
                    )
                ),
            )
        }

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
