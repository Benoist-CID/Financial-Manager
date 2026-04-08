package fr.laforge.benoist.financialmanager.presentation.ui.component

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.presentation.util.getCategoryColor
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
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
    HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 1.dp, modifier = modifier)
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
                contentDescription = "${transaction.description}, ${transaction.amount} euro"
            }
            .clickable { onClicked(transaction) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography

        // 1. Indicator
        AccountIndicator(
            color = transaction.category.getCategoryColor(),
            modifier = Modifier
        )

        Spacer(Modifier.width(12.dp))

        // 2. Text Column (FIXED: Added weight and overflow handling)
        Column(
            modifier = Modifier.weight(1f) // Takes all remaining space between Icon and Amount
        ) {
            Text(
                text = transaction.description,
                style = typography.bodyLarge,
                maxLines = 1, // Prevents expanding height
                overflow = TextOverflow.Ellipsis // Adds "..." if too long
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                val date = if (transaction.isPeriodic) {
                    // Logic to show current month for periodic transactions
                    transaction.dateTime.withMonth(LocalDateTime.now().monthValue)
                } else {
                    transaction.dateTime
                }.format(formatter)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = date, style = typography.titleMedium)
                    if (transaction.syncStatus != SyncStatus.PENDING) {
                        Spacer(Modifier.width(6.dp))
                        SyncStatusChip(syncStatus = transaction.syncStatus)
                    }
                }
            }
        }

        // 3. Amount Area (Pushed to the right by the Column's weight)
        Spacer(Modifier.width(8.dp)) // Small buffer between text and amount

        Row(
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "$currencySign$formattedAmount €",
                style = typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterVertically),
                maxLines = 1
            )
        }

        Spacer(Modifier.width(16.dp))

        // 4. Chevron Icon
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

/**
 * Small chip that indicates the bank-sync state of a transaction.
 *
 * Only shown when [syncStatus] is not [SyncStatus.PENDING] (the default).
 * - [SyncStatus.IN_SYNC] → green "Synced" chip
 * - [SyncStatus.NEW_FROM_BANK] → blue "Bank" chip
 *
 * @param syncStatus The sync state to display.
 * @param modifier   Optional [Modifier].
 */
@Composable
fun SyncStatusChip(syncStatus: SyncStatus, modifier: Modifier = Modifier) {
    val (label, containerColor) = when (syncStatus) {
        SyncStatus.IN_SYNC -> "Synced" to Color(0xFF2E7D32)
        SyncStatus.NEW_FROM_BANK -> "Bank" to Color(0xFF1565C0)
        SyncStatus.PENDING -> return // never shown
    }
    SuggestionChip(
        modifier = modifier,
        onClick = {},
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = containerColor),
    )
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
            HorizontalDivider(color = Color.Black)
        }
    }
}
