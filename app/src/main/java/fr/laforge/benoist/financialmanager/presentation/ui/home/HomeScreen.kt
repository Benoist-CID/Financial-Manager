package fr.laforge.benoist.financialmanager.presentation.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.domain.util.displayDate
import fr.laforge.benoist.financialmanager.domain.util.toDate
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.SwipableTransactionItem
import fr.laforge.benoist.financialmanager.presentation.ui.component.TopBar
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: HomeScreenViewModel = koinViewModel(),
) {
    val allExpenses by vm.allCurrentMonthTransactionsAmount.collectAsState(initial = 0F)
    val recurringExpenses by vm.periodicAmount.collectAsState(initial = 0F)
    val regularExpenses by vm.regularExpenses.collectAsState(initial = 0F)
    val transactions by vm.allTransactions.collectAsState(initial = emptyList())
    val savingsTarget by vm.savingsTarget.collectAsState(initial = 0F)
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()
    val income by vm.income.collectAsState(initial = 0F)

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = displayDate(LocalDateTime.now().toDate()),
                onSave = { vm.saveDb(context) },
                onLoad = { navController.navigate(FinancialManagerScreen.ImportDb.name) },
            )
        }
    ) {
        Column(
            modifier
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding(),
                ),
        ) {
            Column(
                modifier = modifier.background(MaterialTheme.colorScheme.background)
            ) {
                SituationCard(
                    allExpenses = -allExpenses,
                    regularExpenses = -regularExpenses,
                    recurringExpenses = -recurringExpenses,
                    income = income,
                    savingsTarget = savingsTarget
                )

                TextField(
                    value = uiState.query,
                    onValueChange = { newVal -> vm.updateSearch(newVal) },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = { Text("Search") }
                )
            }

            LazyColumn(
                modifier = modifier
                    .background(Color.Red)
            ) {
                items(transactions.filter { transaction ->
                    transaction.description.contains(uiState.query)
                }) { transaction ->
                    SwipableTransactionItem(
                        transaction = transaction,
                        isPeriodic = vm.isPeriodicTransaction(transaction),
                        onClick = {
                            navController.navigate(
                                FinancialManagerScreen.TransactionDetails.name + "/${transaction.uid}",
                            )
                        },
                        onDelete = { shouldDeleteParent ->
                            vm.deleteTransaction(
                                transaction = transaction,
                                shouldDeleteParent = shouldDeleteParent
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    // Need to implement
}
