package fr.laforge.benoist.financialmanager.presentation.ui.transaction.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.DialogType
import fr.laforge.benoist.financialmanager.presentation.ui.component.ShowDialog
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionAmountEditor
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionCategorySelector
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionDescriptionEditor
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionTypeSelector
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTransaction(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: UpdateTransactionViewModel = koinViewModel(),
) {
    val uiState = vm.uiState
    val openAlertDialog = remember { mutableStateOf(false) }
    val openPeriodicAlertDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.update_transaction),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.update_transaction)) },
                icon = {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Favorite",
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    if (vm.isPeriodicTransaction(vm.uiState.transaction)) {
                        openPeriodicAlertDialog.value = true
                    } else {
                        openAlertDialog.value = true
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
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

        // Displays Dialogs if needed
        if (openPeriodicAlertDialog.value) {
            ShowDialog(
                onDismissRequest = {
                    openPeriodicAlertDialog.value = false
                },
                onConfirmation = {
                    vm.updateTransaction(
                        transaction = vm.uiState.transaction,
                        shouldUpdateParent = true
                    )

                    openPeriodicAlertDialog.value = false
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
                },
                onThirdButton = {
                    vm.updateTransaction(
                        transaction = vm.uiState.transaction,
                        shouldUpdateParent = false
                    )
                    openPeriodicAlertDialog.value = false
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
                },
                dialogTitle = stringResource(R.string.update_transaction),
                dialogText = stringResource(R.string.periodic_transaction_update_description),
                icon = {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                },
                confirmButtonText = stringResource(R.string.all),
                dismissButtonText = stringResource(R.string.cancel),
                thirdButtonText = stringResource(R.string.just_this_one),
                dialogType = DialogType.ThreeButtons
            )
        }

        // Displays dialog
        if (openAlertDialog.value) {
            ShowDialog(
                onDismissRequest = {
                    openAlertDialog.value = false
                },
                onConfirmation = {
                    vm.updateTransaction(
                        transaction = vm.uiState.transaction,
                        shouldUpdateParent = false
                    )
                    openAlertDialog.value = false
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
                },
                dialogTitle = stringResource(R.string.update_transaction),
                dialogText = stringResource(R.string.update_warning_message),
                icon = {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                },
            )
        }
    }
}

@Composable
@Preview
fun UpdateTransactionPreview() {
    UpdateTransaction(navController = rememberNavController())
}
