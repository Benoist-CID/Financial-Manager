package fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.TransactionsListScreen
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonRecurringExpensesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: NonRecurringManagementViewModel = koinViewModel(),
) {
    // Collect Data
    val transactions by vm.expenseItems.collectAsState()
    val totalMonthly by vm.totalExpensesMonthly.collectAsState()
    val query by vm.query.collectAsState()

    TransactionsListScreen(
        title = stringResource(R.string.label_variable_expenses),
        transactions = transactions,
        totalMonthly = totalMonthly,
        query = query,
        onQueryChange = { vm.updateSearch(it) },
        onBackClick = { navController.popBackStack() },
        onItemClick = { transaction ->
            navController.navigate(
                FinancialManagerScreen.TransactionDetails.name + "/${transaction.uid}"
            )
        },
        onItemDelete = { transaction ->
            vm.deleteTransaction(transaction)
        }
    )
}
