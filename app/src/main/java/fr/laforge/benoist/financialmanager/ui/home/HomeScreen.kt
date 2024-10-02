package fr.laforge.benoist.financialmanager.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.component.ShowDialog
import fr.laforge.benoist.financialmanager.ui.component.TopBar
import fr.laforge.benoist.financialmanager.ui.component.TransactionRow
import fr.laforge.benoist.financialmanager.util.displayDate
import fr.laforge.benoist.financialmanager.util.toDate
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
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

                Timber.d("allExpenses: $allExpenses, income: $income, savingsTarget: $savingsTarget")
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
                    val dismissState = rememberDismissState()
                    val openAlertDialog = remember { mutableStateOf(false) }

                    if (dismissState.isDismissed(direction = DismissDirection.EndToStart)) {
                        openAlertDialog.value = true
                    }
                    // Displays dialog
                    when {
                        openAlertDialog.value ->
                            ShowDialog(
                                onDismissRequest = {
                                    vm.viewModelScope.launch {
                                        dismissState.snapTo(DismissValue.Default)
                                    }
                                    openAlertDialog.value = false
                                },
                                onConfirmation = {
                                    vm.deleteTransaction(transaction = transaction)
                                    vm.viewModelScope.launch {
                                        dismissState.snapTo(DismissValue.Default)
                                    }
                                    openAlertDialog.value = false
                                },
                                dialogTitle = stringResource(R.string.do_you_want_to_delete),
                                dialogText = stringResource(R.string.delete_warning_message),
                                icon = {
                                    Icon(Icons.Filled.Delete, contentDescription = null)
                                },
                            )
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.EndToStart,
                        ),
                        background = {
                            // this background is visible when we swipe.
                            // it contains the icon
                            // background color
                            val backgroundColor by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.6f)
                                    else -> Color.White
                                },
                                label = "",
                            )
                            // icon size
                            val iconScale by animateFloatAsState(
                                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 1.3f else 0.5f,
                                label = "",
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color = backgroundColor)
                                    .padding(end = 16.dp), // inner padding
                                contentAlignment = Alignment.CenterEnd, // place the icon at the end (left)
                            ) {
                                Icon(
                                    modifier = Modifier.scale(iconScale),
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                )
                            }
                        },
                        dismissContent = {
                            // list item
                            TransactionRow(transaction) { transaction ->
                                navController.navigate(
                                    FinancialManagerScreen.TransactionDetails.name + "/${transaction.uid}",
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
}
