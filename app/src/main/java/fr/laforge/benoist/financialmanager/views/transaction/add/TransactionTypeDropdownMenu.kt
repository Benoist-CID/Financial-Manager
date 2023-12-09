package fr.laforge.benoist.financialmanager.views.transaction.add

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTypeDropdownMenu(
    title: String,
    data: List<String>,
    modifier: Modifier = Modifier,
    defaultValue: String = data[0],
    onSelectedValueChanged: (String) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var selectedData by remember {
        mutableStateOf(defaultValue)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newValue ->
            isExpanded = newValue
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedData,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            placeholder = {
                Text(text = "Please select your transaction type")
            },
            modifier = Modifier.menuAnchor(),
            label = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            data.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(text = type)
                    },
                    onClick = {
                        selectedData = type
                        onSelectedValueChanged(type)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionTypeDropdownMenuPreview() {
    GenericTypeDropdownMenu(
        title = stringResource(id = R.string.type),
        data = TransactionType.values().map{ it.toString() }
    ) {
    }
}
