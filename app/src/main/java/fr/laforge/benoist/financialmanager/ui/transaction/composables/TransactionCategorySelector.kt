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
import fr.laforge.benoist.model.TransactionCategory
import timber.log.Timber

@Composable
fun TransactionCategorySelector(
    modifier: Modifier = Modifier,
    initialValue: TransactionCategory = TransactionCategory.None,
    onTransactionCategoryChanged: (TransactionCategory) -> Unit
) {
    Timber.d("TransactionCategorySelector: $initialValue")
    GenericTypeDropdownMenu(
        title = stringResource(R.string.category),
        data = TransactionCategory.entries.map { it.toString() },
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        defaultValue = initialValue.name
    ) { transactionCategory ->
        val category: TransactionCategory =  TransactionCategory.from(name = transactionCategory)?: TransactionCategory.None
        onTransactionCategoryChanged(category)
    }
}

@Composable
@Preview(showBackground = true)
fun TransactionCategorySelectorPreview() {
    TransactionCategorySelector{}
}
