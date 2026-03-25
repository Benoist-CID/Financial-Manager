package fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring

import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringExpenseTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringIncomeTransactionsUseCase
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.AbstractTransactionManagementViewModel

/**
 * [ViewModel] for the "Non-Recurring Transactions" management screen.
 *
 * Manages variable discretionary spending (Expenses) and one-off extra revenue (Incomes).
 */
class NonRecurringManagementViewModel(
    getNonRecurringExpenseTransactionsUseCase: GetNonRecurringExpenseTransactionsUseCase,
    getNonRecurringIncomeTransactionsUseCase: GetNonRecurringIncomeTransactionsUseCase,
    deleteTransactionUseCase: DeleteTransactionUseCase,
) : AbstractTransactionManagementViewModel(
    expensesFlow = getNonRecurringExpenseTransactionsUseCase(),
    incomesFlow = getNonRecurringIncomeTransactionsUseCase(),
    deleteTransactionUseCase = deleteTransactionUseCase
)
