package fr.laforge.benoist.financialmanager.presentation.ui.transaction.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionAmountEditor
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionCategorySelector
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionDescriptionEditor
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.composables.TransactionTypeSelector
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: AddTransactionViewModel = koinViewModel(),
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_transaction),
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
                text = { Text(text = stringResource(id = R.string.create_input)) },
                icon = {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Favorite",
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    vm.createTransaction()
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
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

@Preview
@Composable
fun AddTransactionScreenPreview() {
    AddTransactionScreen(navController = rememberNavController())
}
