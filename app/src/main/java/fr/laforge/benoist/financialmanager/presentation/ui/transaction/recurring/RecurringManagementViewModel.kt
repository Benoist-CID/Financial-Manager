package fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring

import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringExpenseTemplatesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringIncomeTransactionsUseCase
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.AbstractTransactionManagementViewModel

/**
 * [ViewModel] for the "Recurring Transactions" management screen.
 *
 * Manages fixed monthly costs (Expenses) and regular paychecks (Incomes) using templates.
 */
class RecurringManagementViewModel(
    getRecurringExpenseTemplatesUseCase: GetRecurringExpenseTemplatesUseCase,
    getRecurringIncomeTransactionsUseCase: GetRecurringIncomeTransactionsUseCase,
    deleteTransactionUseCase: DeleteTransactionUseCase,
) : AbstractTransactionManagementViewModel(
    expensesFlow = getRecurringExpenseTemplatesUseCase(),
    incomesFlow = getRecurringIncomeTransactionsUseCase(),
    deleteTransactionUseCase = deleteTransactionUseCase
)
