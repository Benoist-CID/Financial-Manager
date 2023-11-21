package fr.laforge.benoist.financialmanager

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.laforge.benoist.financialmanager.views.addinput.AddTransactionViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddTransactionViewModel()
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [GeneratorApp].
 */
fun CreationExtras.generatorApplication(): FinancialManagerApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FinancialManagerApp)
