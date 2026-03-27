package fr.laforge.benoist.financialmanager.presentation.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.SwipableTransactionItem
import fr.laforge.benoist.financialmanager.presentation.ui.component.TopBar
import fr.laforge.benoist.financialmanager.presentation.ui.home.situation.card.SituationCard
import fr.laforge.benoist.financialmanager.presentation.ui.pending.PendingTransactionsViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: HomeScreenViewModel = koinViewModel(),
    pendingVm: PendingTransactionsViewModel = koinViewModel(),
) {
    val transactions by vm.uiListFlow.collectAsState(initial = emptyList())
    val uiState by vm.uiState.collectAsState()
    val pendingUiState by pendingVm.uiState.collectAsState()
    val pendingCount = pendingUiState.transactions.size

    val listState = rememberLazyListState()

    val isScrollingUp = listState.isScrollingUp()

    val isExpanded = listState.firstVisibleItemIndex == 0 || isScrollingUp

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                query = uiState.query,
                onQueryChange = { newVal -> vm.updateSearch(newVal) },
                pendingCount = pendingCount,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Transaction") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = { navController.navigate(FinancialManagerScreen.AddInput.name) },
                expanded = isExpanded,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                ),
        ) {
            Column(
                modifier = modifier.background(MaterialTheme.colorScheme.background)
            ) {
                SituationCard {
                    navController.navigate(FinancialManagerScreen.Indicators.name)
                }
            }

            LazyColumn(
                state = listState,
                modifier = modifier,
                contentPadding = PaddingValues(
                    top = 0.dp,
                    // 88.dp = 56dp (FAB) + 16dp (Margin) + 16dp (Breathing room)
                    bottom = paddingValues.calculateBottomPadding() + 88.dp,
                    start = 0.dp,
                    end = 0.dp
                )
            ) {
                items(transactions) { transaction ->
                    SwipableTransactionItem(
                        transaction = transaction,
                        isPeriodic = vm.isPeriodicTransaction(transaction),
                        onClick = {
                            if (transaction.uid > -1) {
                                navController.navigate(
                                    FinancialManagerScreen.TransactionDetails.name + "/${transaction.uid}",
                                )
                            }
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

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
