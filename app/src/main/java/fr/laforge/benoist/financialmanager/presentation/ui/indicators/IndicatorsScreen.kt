package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.presentation.ui.component.IndicatorBar

@Composable
fun IndicatorsScreen(
    income: Float,
    recurringExpenses: Float,
    regularExpenses: Float,
    // Add other params or ViewModel here
) {
    // 1. Calculate derived data
    val totalExpenses = recurringExpenses + regularExpenses
    val remainingBalance = income - totalExpenses

    // 2. Define Reference for bars (Income is usually the baseline 100%)
    // If income is 0, use total expenses to avoid empty bars
    val maxReference = if (income > 0) income else totalExpenses

    Scaffold(
        topBar = {
            // Your TopBar here
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
                .verticalScroll(rememberScrollState()) // Allow scrolling
        ) {

            // Section 1: Money In
            Text(
                text = "Income",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            IndicatorBar(
                label = "Total Income",
                amount = income,
                totalReference = maxReference,
                color = Color(0xFF4CAF50) // Green
            )

//            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

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

            // Optional: Total Expenses Bar
            IndicatorBar(
                label = "Total Expenses",
                amount = totalExpenses,
                totalReference = maxReference,
                color = Color(0xFFD32F2F) // Darker Red
            )

//            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Section 3: Result
            Text(
                text = "Result",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            // Logic to change color if negative
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
    IndicatorsScreen(
        income = 1000F,
        recurringExpenses = 250F,
        regularExpenses = 300F,
    )
}
