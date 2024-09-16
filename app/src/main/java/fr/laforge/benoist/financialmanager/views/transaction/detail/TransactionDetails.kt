package fr.laforge.benoist.financialmanager.views.transaction.detail

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionAmountEditor
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionCategorySelector
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionDescriptionEditor
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionTypeSelector
import fr.laforge.benoist.model.TransactionType
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionDetails(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: TransactionDetailsViewModel,
) {
    val uiState by vm.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> { vm.getTransaction() }
        }
    }

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
                TransactionTypeSelector(initialValue = TransactionType.Expense, modifier = modifier) { transactionType ->
//                vm.updateInputType(transactionType = transactionType)
                }

                TransactionCategorySelector(
                    initialValue = uiState.transactionCategory,
                    modifier = modifier
                ) { category ->
//                vm.updateTransactionCategory(transactionCategory = category)
                }

                TransactionAmountEditor(
                    modifier = modifier,
                    initialValue = uiState.amount.toDouble()
                ) { newVal ->
//                vm.updateAmount(amount = newVal.toString())
                }

                TransactionDescriptionEditor(
                    modifier = modifier,
                    initialValue = uiState.description/* uiState.description*/
                ) { newDescription ->
//                vm.updateDescription(newDescription)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TransactionDetailsPreview() {
    TransactionDetails(
        navController = rememberNavController(),
        vm = TransactionDetailsViewModel(0)
        )
}
