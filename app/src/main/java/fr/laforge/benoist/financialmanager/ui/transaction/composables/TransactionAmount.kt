package fr.laforge.benoist.financialmanager.ui.transaction.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    initialValue: String = "0.0",
    onAmountChanged: (String) -> Unit,
) {
    var isError by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        value = initialValue,
        onValueChange = { newVal ->
            try {
                newVal.toFloat()
                isError = false
            } catch (e: Exception) {
                isError = true
            }
            onAmountChanged(newVal)
        },
        label = {
            Text(
                text = stringResource(id = R.string.amount),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        supportingText = {
            if (isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Incorrect value",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            if (isError)
                Icon(Icons.Filled.Clear,"error", tint = MaterialTheme.colorScheme.error)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
@Preview(showBackground = true)
fun TransactionAmountPreview() {
    TransactionAmountEditor(initialValue = "10.0") {}
}
