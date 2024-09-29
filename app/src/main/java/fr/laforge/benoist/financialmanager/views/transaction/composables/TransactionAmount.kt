package fr.laforge.benoist.financialmanager.views.transaction.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import timber.log.Timber

@Composable
fun TransactionAmountEditor(
    modifier: Modifier = Modifier,
    initialValue: Float = 0.0F,
    onAmountChanged: (Double) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        value = initialValue.toString(),
        onValueChange = { newVal -> onAmountChanged(newVal.toDouble()) },
        label = {
            Text(
                text = stringResource(id = R.string.amount),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
@Preview(showBackground = true)
fun TransactionAmountPreview() {
    TransactionAmountEditor(initialValue = 10.0F) {

    }
}
