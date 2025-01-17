package fr.laforge.benoist.financialmanager.ui.transaction.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.transaction.add.GenericTypeDropdownMenu
import fr.laforge.benoist.model.TransactionType
import timber.log.Timber

/**
 * Displays a drop down selector for the Transaction type
 */
@Composable
fun TransactionTypeSelector(
    modifier: Modifier = Modifier,
    initialValue: TransactionType = TransactionType.Expense,
    onTypeChanged: (TransactionType) -> Unit,
) {
    Timber.d("Transaction type: $initialValue")

    GenericTypeDropdownMenu(
        title = stringResource(R.string.type),
        data = TransactionType.entries.map { it.toString() },
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        defaultValue = initialValue.name
    ) { transactionType ->
        onTypeChanged(TransactionType.from(name = transactionType) ?: TransactionType.Expense)
    }
}

@Composable
@Preview(showBackground = true)
fun TransactionTypeSelectorPreview() {
    TransactionTypeSelector(initialValue = TransactionType.Income){}
}
