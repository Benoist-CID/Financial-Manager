package fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetails(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: TransactionDetailsViewModel = koinViewModel(),
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.transaction_details),
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
                text = { Text(text = stringResource(id = R.string.edit)) },
                icon = {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    navController.navigate(FinancialManagerScreen.UpdateTransaction.name + "/${uiState.transaction.uid}")
                }
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
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
    )
}
