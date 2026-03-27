package fr.laforge.benoist.financialmanager.presentation.ui.settings.notificationformat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.notification.DescriptionSource
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Screen that lists all user-defined notification formats and allows the user to add or
 * delete them.
 *
 * A floating action button opens [AddNotificationFormatDialog]. Each card in the list
 * shows the format's description, pattern, and description source, with a delete icon.
 *
 * @param navController Navigation controller used to handle back navigation.
 * @param modifier       Optional [Modifier] applied to the root [Scaffold].
 * @param vm             [NotificationFormatViewModel] injected via Koin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationFormatScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: NotificationFormatViewModel = koinViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notification_formats)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_notification_format),
                )
            }
        },
    ) { paddingValues ->
        if (uiState.formats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_notification_formats),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(uiState.formats, key = { it.id.toString() }) { format ->
                    NotificationFormatCard(
                        format = format,
                        onDelete = { vm.deleteFormat(format.id) },
                    )
                }
                item { Spacer(Modifier.height(72.dp)) } // FAB clearance
            }
        }
    }

    if (showAddDialog) {
        AddNotificationFormatDialog(
            onConfirm = { description, pattern, source ->
                vm.addFormat(description, pattern, source)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

/**
 * Card showing a single [NotificationFormat]'s description, pattern, and description source
 * with a delete action.
 *
 * @param format   The format to display.
 * @param onDelete Called when the user taps the delete icon.
 * @param modifier Optional [Modifier].
 */
@Composable
private fun NotificationFormatCard(
    format: NotificationFormat,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = format.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = format.pattern,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                val sourceLabel = when (format.descriptionSource) {
                    DescriptionSource.BODY -> stringResource(R.string.description_source_body)
                    DescriptionSource.TITLE -> stringResource(R.string.description_source_title)
                }
                Text(
                    text = stringResource(R.string.description_source_label, sourceLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_notification_format),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

/**
 * Dialog that collects a description, pattern, and description source for a new format.
 *
 * The confirm button is disabled until the description is non-blank and the pattern contains
 * the required `{amount}` placeholder. A radio group lets the user choose whether the
 * transaction description is read from the notification body or the notification title.
 *
 * @param onConfirm Called with (description, pattern, descriptionSource) when the user confirms.
 * @param onDismiss Called when the user cancels.
 */
@Composable
private fun AddNotificationFormatDialog(
    onConfirm: (description: String, pattern: String, source: DescriptionSource) -> Unit,
    onDismiss: () -> Unit,
) {
    var description by rememberSaveable { mutableStateOf("") }
    var pattern by rememberSaveable { mutableStateOf("") }
    var descriptionSource by rememberSaveable { mutableStateOf(DescriptionSource.BODY) }

    val isValid = description.isNotBlank() && pattern.contains("{amount}")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_notification_format)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.notification_format_description)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = pattern,
                    onValueChange = { pattern = it },
                    label = { Text(stringResource(R.string.notification_format_pattern)) },
                    placeholder = { Text("Spent {amount} at {description}") },
                    supportingText = {
                        Text(
                            text = stringResource(R.string.notification_format_pattern_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.description_source_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Column(modifier = Modifier.selectableGroup()) {
                    DescriptionSource.entries.forEach { source ->
                        val label = when (source) {
                            DescriptionSource.BODY -> stringResource(R.string.description_source_body)
                            DescriptionSource.TITLE -> stringResource(R.string.description_source_title)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = descriptionSource == source,
                                    onClick = { descriptionSource = source },
                                    role = Role.RadioButton,
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = descriptionSource == source,
                                onClick = null,
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(description.trim(), pattern.trim(), descriptionSource) },
                enabled = isValid,
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
