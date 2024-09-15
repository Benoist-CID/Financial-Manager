package fr.laforge.benoist.financialmanager.views.transaction.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R


@Composable
fun TransactionDescriptionEditor(
    modifier: Modifier = Modifier,
    initialValue: String = "",
    onDescriptionChanged: (String) -> Unit,
)  {
    OutlinedTextField(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        value = initialValue,
        onValueChange = { newDescription -> onDescriptionChanged(newDescription) },
        label = {
            Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
    )
}

@Composable
@Preview(showBackground = true)
fun TransactionDescriptionEditorPreview() {
    TransactionDescriptionEditor(initialValue = "A simple description") {

    }
}
