package fr.laforge.benoist.financialmanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.util.displayDate
import fr.laforge.benoist.financialmanager.util.toDate
import fr.laforge.benoist.financialmanager.util.toLocalDateTime
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    label: String,
    modifier: Modifier = Modifier,
    defaultDate: LocalDateTime = LocalDateTime.now(),
    onDateChanged: (LocalDateTime) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = defaultDate.toDate().time
        )

    OutlinedTextField(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable {
                showDialog = true
            },
        value = displayDate(Date(datePickerState.selectedDateMillis!!)),
        onValueChange = { },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        enabled = false,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            //For Icons
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = { Button(onClick = {
                showDialog = false
                onDateChanged(Date(datePickerState.selectedDateMillis!!).toLocalDateTime())
            }) {
                Text(text = stringResource(android.R.string.ok))
            } },
            dismissButton = { Button(onClick = {showDialog = false}) {
                Text(text = stringResource(android.R.string.cancel))
            } },
            content = {
                DatePicker(
                    state = datePickerState
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerComponentPreview() {
    DatePickerComponent(
        label = "Start date",
        defaultDate = LocalDateTime.now().plusMonths(2)
    ) {
        Timber.d("$it")
    }
}