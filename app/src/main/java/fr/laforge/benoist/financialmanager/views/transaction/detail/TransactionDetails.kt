package fr.laforge.benoist.financialmanager.views.transaction.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionAmountEditor
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionCategorySelector
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionDescriptionEditor
import fr.laforge.benoist.financialmanager.views.transaction.composables.TransactionTypeSelector

@Composable
fun TransactionDetails(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: TransactionDetailsViewModel,
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.popBackStack(FinancialManagerScreen.Home.name, false)
                }
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                )
            }
        },
    ) {
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
                Row(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.type)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = uiState.transaction.type.name,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.category)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = uiState.transaction.category.name,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.amount)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = uiState.transaction.amount.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(R.string.description)
                    )

                    Spacer(modifier = modifier.weight(1f))

                    Text(
                        text = uiState.transaction.description,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
//        Column {
//            Text(
//                text = stringResource(id = R.string.create_transaction),
//                fontSize = 30.sp,
//                modifier = Modifier.padding(
//                    top = it.calculateTopPadding() + 16.dp,
//                    start = it.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
//                    bottom = 40.dp
//                )
//            )
//
//            Column(
//                modifier = modifier
//                    .verticalScroll(rememberScrollState())
//                    .padding(bottom = it.calculateBottomPadding() + 16.dp)
//            ) {
//                TransactionTypeSelector(
//                    modifier = modifier,
//                    initialValue = uiState.transaction.type,
//                ) { transactionType ->
//
//                }
//
//                TransactionCategorySelector(
//                    modifier = modifier,
//                    initialValue = uiState.transaction.category,
//                ) { category ->
//
//                }
//
//                TransactionAmountEditor(
//                    modifier = modifier,
//                    initialValue = uiState.transaction.amount
//                ) { newVal ->
//
//                }
//
//                TransactionDescriptionEditor(
//                    modifier = modifier,
//                    initialValue = uiState.transaction.description/* uiState.description*/
//                ) { newDescription ->
//
//                }
//            }
//        }
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
