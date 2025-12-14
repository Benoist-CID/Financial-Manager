package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.presentation.ui.component.IndicatorBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun IndicatorsScreen(
    // Injected automatically by Koin
    vm: IndicatorsViewModel = koinViewModel(),
) {
    val recurringIncome by vm.recurringIncome.collectAsState(initial = 0f)
    val recurringExpenses by vm.recurringExpenses.collectAsState(initial = 0f)

    val regularExpenses = 0f


    val totalIncome = recurringIncome

    val totalExpenses = recurringExpenses + regularExpenses
    val remainingBalance = totalIncome - totalExpenses

    // Define Reference for bars (Income is baseline 100%)
    val maxReference = if (totalIncome > 0) totalIncome else 1f // Avoid division by zero

    Scaffold(
        topBar = {
            Text(
                text = "Financial Indicators",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Section 1: Money In
            Text(
                text = "Income",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            IndicatorBar(
                label = "Recurring Income", // Updated label to be precise
                amount = totalIncome,
                totalReference = maxReference,
                color = Color(0xFF4CAF50) // Green
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Section 2: Money Out
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            IndicatorBar(
                label = "Recurring (Bills, Subs)",
                amount = recurringExpenses,
                totalReference = maxReference,
                color = Color(0xFFFF9800) // Orange
            )

            IndicatorBar(
                label = "Variable Expenses",
                amount = regularExpenses,
                totalReference = maxReference,
                color = Color(0xFFF44336) // Red
            )

            IndicatorBar(
                label = "Total Expenses",
                amount = totalExpenses,
                totalReference = maxReference,
                color = Color(0xFFD32F2F) // Darker Red
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Section 3: Result
            Text(
                text = "Result",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            val balanceColor = if (remainingBalance >= 0) Color(0xFF2196F3) else Color.Red

            IndicatorBar(
                label = "Remaining Balance",
                amount = remainingBalance,
                totalReference = maxReference,
                color = balanceColor
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun IndicatorsScreenPreview() {
//    IndicatorsScreen(
//        income = 1000F,
//        recurringExpenses = 250F,
//        regularExpenses = 300F,
//    )
}
