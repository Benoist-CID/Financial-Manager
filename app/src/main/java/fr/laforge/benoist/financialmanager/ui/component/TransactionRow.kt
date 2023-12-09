package fr.laforge.benoist.financialmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.util.getAmountColor
import fr.laforge.benoist.financialmanager.util.getAmountFontWeight
import fr.laforge.benoist.financialmanager.util.getAmountTextStyle
import fr.laforge.benoist.financialmanager.util.getFinancialInputText
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.model.Transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onClicked: (Transaction) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .clickable {
                onClicked(transaction)
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = transaction.description,
                fontSize = 20.sp,
            )

            Text(
                text = transaction.getFinancialInputText(),
                color = transaction.getAmountColor(),
                fontWeight = transaction.getAmountFontWeight(),
                style = transaction.getAmountTextStyle(),
                modifier = Modifier.padding(1.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            val date = if (transaction.isPeriodic) {
                transaction.dateTime.withMonth(LocalDateTime.now().monthValue)
            } else {
                transaction.dateTime
            }.format(formatter)
            Text(text = date)

            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun FinancialInputRowPreview() {
    val transactions = mutableListOf(
            Transaction(
                description = "An income", type = TransactionType.Income, amount = 10.0F
            ),

            Transaction(
                description = "An expense", type = TransactionType.Expense, amount = 10.0F
            )
    )

    LazyColumn {
        items(transactions) { input ->
            TransactionRow(input)
        }
    }
}
