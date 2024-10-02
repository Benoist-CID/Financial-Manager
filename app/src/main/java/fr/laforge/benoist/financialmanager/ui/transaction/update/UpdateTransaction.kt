package fr.laforge.benoist.financialmanager.ui.transaction.update

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
import fr.laforge.benoist.financialmanager.ui.transaction.add.PeriodicalTransactionComponent
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionAmountEditor
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionCategorySelector
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionDescriptionEditor
import fr.laforge.benoist.financialmanager.ui.transaction.composables.TransactionTypeSelector
import fr.laforge.benoist.financialmanager.ui.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.model.TransactionPeriod

@Composable
fun UpdateTransaction(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: UpdateTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState = vm.uiState

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.update_transaction)) },
                icon = {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Favorite",
                    )
                },
                onClick = {
                    vm.updateTransaction()
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
                }
            )
        },
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.update_transaction),
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
                TransactionTypeSelector(
                    initialValue = uiState.transaction.type
                ) { transactionType ->
                    vm.updateInputType(transactionType = transactionType)
                }

                TransactionCategorySelector(initialValue = uiState.transaction.category) { category ->
                    vm.updateTransactionCategory(transactionCategory = category)
                }

                TransactionAmountEditor(initialValue = uiState.transaction.amount.toString()) { newVal ->
                    vm.updateAmount(amount = newVal)
                }

                TransactionDescriptionEditor(initialValue = uiState.transaction.description) { newDescription ->
                    vm.updateDescription(newDescription)
                }
            }
        }
    }
}

@Composable
@Preview
fun UpdateTransactionPreview() {
    UpdateTransaction(navController = rememberNavController())
}
