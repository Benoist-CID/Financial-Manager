package fr.laforge.benoist.financialmanager.ui.transaction.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import timber.log.Timber

@Composable
fun TransactionDetails(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: TransactionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(FinancialManagerScreen.UpdateTransaction.name + "/${uiState.transaction.uid}")
                }
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                )
            }
        },
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.transaction_details),
                fontSize = 30.sp,
                modifier = Modifier.padding(
                    top = it.calculateTopPadding() + 16.dp,
                    start = it.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                    bottom = 40.dp
                )
            )

            Card(
                modifier = Modifier.padding(
                    top = it.calculateTopPadding() + 16.dp,
                    start = it.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                    end = it.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                    bottom = 40.dp
                ), colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = modifier.fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TransactionRow(
                        labelId = R.string.type,
                        text = uiState.transaction.type.name,
                        modifier = Modifier.padding(8.dp)
                    )

                    TransactionRow(
                        labelId = R.string.category,
                        text = uiState.transaction.category.name,
                        modifier = Modifier.padding(8.dp)
                    )

                    TransactionRow(
                        labelId = R.string.amount,
                        text = uiState.transaction.amount.toString(),
                        modifier = Modifier.padding(8.dp)
                    )

                    TransactionRow(
                        labelId = R.string.description,
                        text = uiState.transaction.description,
                        modifier = Modifier.padding(8.dp)
                    )
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
//        vm = TransactionDetailsViewModel()
    )
}
