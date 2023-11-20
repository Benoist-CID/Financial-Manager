package fr.laforge.benoist.financialmanager.ui.component

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.util.getAmountColor
import fr.laforge.benoist.financialmanager.util.getAmountFontWeight
import fr.laforge.benoist.financialmanager.util.getAmountTextStyle
import fr.laforge.benoist.financialmanager.util.getFinancialInputText
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType
import java.time.format.DateTimeFormatter


@Composable
fun FinancialInputRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onClicked: (Transaction) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
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
            Text(text = transaction.dateTime.format(formatter))

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
                description = "An income", type = InputType.Income, amount = 10.0F
            ),

            Transaction(
                description = "An expense", type = InputType.Expense, amount = 10.0F
            )
    )

    LazyColumn {
        items(transactions) { input ->
            FinancialInputRow(input)
        }
    }
}
