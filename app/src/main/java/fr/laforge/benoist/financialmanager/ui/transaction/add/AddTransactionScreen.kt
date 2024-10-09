package fr.laforge.benoist.financialmanager.ui.transaction.add

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.AppViewModelProvider
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionAmountEditor
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionCategorySelector
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionDescriptionEditor
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionTypeSelector

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddTransactionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: AddTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.create_input)) },
                icon = {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Favorite",
                    )
                },
                onClick = {
                    vm.createTransaction()
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
                }
            )
        },
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.create_transaction),
                fontSize = 30.sp,
                modifier = Modifier.padding(
                    top = it.calculateTopPadding() + 16.dp,
                    start = it.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                    bottom = 40.dp
                )
            )

            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = it.calculateBottomPadding() + 16.dp)
            ) {
                TransactionTypeSelector { transactionType ->
                    vm.updateInputType(transactionType = transactionType)
                }

                TransactionCategorySelector { category ->
                    vm.updateTransactionCategory(transactionCategory = category)
                }

                TransactionAmountEditor(initialValue = uiState.amount) { newVal ->
                    vm.updateAmount(amount = newVal)
                }

                TransactionDescriptionEditor(initialValue = uiState.description) { newDescription ->
                    vm.updateDescription(newDescription)
                }

                PeriodicalTransactionComponent(
                    defaultPeriod = vm.period,
                    onCheckedChange = { checked -> vm.updateIsPeriodic(newState = checked) },
                    onPeriodChanged = { period -> vm.updatePeriod(newPeriod = period) },
                    onStartDateChanged = { date -> vm.updateStartDate(newDate = date) }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddTransactionScreenPreview() {
    AddTransactionScreen(navController = rememberNavController())
}
