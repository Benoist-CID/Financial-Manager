package fr.laforge.benoist.financialmanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.util.getCategoryColor
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionType
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")

/**
 * A vertical colored line that is used in a [BaseRow] to differentiate accounts.
 */
@Composable
private fun AccountIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color)
    )
}

@Composable
fun RallyDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp, modifier = modifier)
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    onClicked: (Transaction) -> Unit = {}
) {
    val currencySign = if (transaction.type == TransactionType.Expense) "-" else ""
    val formattedAmount = formatAmount(transaction.amount)
    Row(
        modifier = modifier
            .height(68.dp)
            .background(MaterialTheme.colorScheme.background)
            .clearAndSetSemantics {
                contentDescription =
                    ""
            }.clickable { onClicked(transaction) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        AccountIndicator(
            color = transaction.category.getCategoryColor(),
            modifier = Modifier
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier) {
            Text(text = transaction.description, style = typography.bodyLarge)
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                val date = if (transaction.isPeriodic) {
                    transaction.dateTime.withMonth(LocalDateTime.now().monthValue)
                } else {
                    transaction.dateTime
                }.format(formatter)
                Text(text = date, style = typography.titleMedium)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currencySign,
                style = typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = formattedAmount,
                style = typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = "€",
                style = typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
    }
    RallyDivider()
}

@Preview(
    showBackground = true,
)
@Composable
fun FinancialInputRowPreview() {
    val transactions = mutableListOf(
        Transaction(
            description = "An income",
            type = TransactionType.Income,
            amount = 10.0F,
            category = TransactionCategory.EducationAndFamily
        ),

        Transaction(
            description = "An expense",
            type = TransactionType.Expense,
            amount = 10.0F,
            category = TransactionCategory.Accommodation
        )
    )

    LazyColumn {
        items(transactions) { input ->
            TransactionRow(input)
            Divider(color = Color.Black)
        }
    }
}
