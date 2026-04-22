package fr.laforge.benoist.financialmanager.presentation.ui.home.upcoming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.UpcomingExpense
import fr.laforge.benoist.financialmanager.presentation.ui.component.formatAmount
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM")

/**
 * Home-screen card that lists every expense due between now and the end of the
 * current month, together with the total amount outstanding.
 *
 * The card is driven by [UpcomingExpensesViewModel] and has no business logic of
 * its own. An empty state message is shown when no expenses are upcoming.
 *
 * @param modifier Optional [Modifier] applied to the root container.
 * @param vm       ViewModel providing upcoming expenses state. Resolved via Koin by default.
 */
@Composable
fun UpcomingExpensesCard(
    modifier: Modifier = Modifier,
    vm: UpcomingExpensesViewModel = koinViewModel(),
) {
    val expenses by vm.upcomingExpenses.collectAsStateWithLifecycle()
    val total by vm.totalUpcomingAmount.collectAsStateWithLifecycle()

    UpcomingExpensesContent(
        modifier = modifier,
        expenses = expenses,
        total = total,
    )
}

@Composable
private fun UpcomingExpensesContent(
    expenses: List<UpcomingExpense>,
    total: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.upcoming_expenses_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${formatAmount(total)} €",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        if (expenses.isEmpty()) {
            Text(
                text = stringResource(R.string.upcoming_expenses_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            expenses.forEach { expense ->
                UpcomingExpenseRow(expense = expense)
            }
        }
    }
}

@Composable
private fun UpcomingExpenseRow(expense: UpcomingExpense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = expense.date.format(DATE_FORMATTER),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = expense.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${formatAmount(expense.amount)} €",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpcomingExpensesContentPopulatedPreview() {
    val now = LocalDateTime.now()
    UpcomingExpensesContent(
        expenses = listOf(
            UpcomingExpense(uid = 1, date = now.plusDays(2), amount = 9.99f, description = "Netflix", category = TransactionCategory.None, isRecurring = true),
            UpcomingExpense(uid = 2, date = now.plusDays(5), amount = 80f, description = "Electricity", category = TransactionCategory.None, isRecurring = false),
            UpcomingExpense(uid = 3, date = now.plusDays(8), amount = 650f, description = "Rent", category = TransactionCategory.None, isRecurring = true),
        ),
        total = 739.99f,
    )
}

@Preview(showBackground = true)
@Composable
private fun UpcomingExpensesContentEmptyPreview() {
    UpcomingExpensesContent(
        expenses = emptyList(),
        total = 0f,
    )
}
