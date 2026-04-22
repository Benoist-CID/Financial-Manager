package fr.laforge.benoist.financialmanager.presentation.ui.sync

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.presentation.ui.component.formatAmount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

/**
 * Screen that presents bank-sync match candidates for user review.
 *
 * States:
 * - **Idle**: Shows an "Import CSV" button that opens the system file picker.
 * - **Loading**: Shows a progress indicator while parsing and matching.
 * - **No matches**: Shows a confirmation that sync found nothing to review.
 * - **Matches**: A list of [MatchResultCard]s, each with Confirm, Skip and Import actions.
 * - **Error**: Shows the error message with a button to return to idle and retry.
 *
 * File reading is performed in the composable using [LocalContext] so that the ViewModel
 * remains free of Android dependencies. The decoded CSV string is passed to
 * [SyncReviewViewModel.importCsv] for parsing and matching.
 *
 * @param navController Used for back navigation.
 * @param modifier       Optional [Modifier] for the root [Scaffold].
 * @param vm             [SyncReviewViewModel] injected via Koin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncReviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: SyncReviewViewModel = koinViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    // URI of the file chosen by the user; drives the LaunchedEffect below.
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // Opens the OS file picker. Accepts any MIME type because bank CSV exports vary widely.
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? -> selectedUri = uri }

    // Reads the file on IO thread and hands the decoded text to the ViewModel.
    // ISO-8859-1 is used because Banque Populaire exports are typically in that encoding.
    LaunchedEffect(selectedUri) {
        val uri = selectedUri ?: return@LaunchedEffect
        val content = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)
                ?.bufferedReader(Charsets.ISO_8859_1)
                ?.use { it.readText() }
        }
        selectedUri = null
        if (content != null) vm.importCsv(content)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sync_review_title)) },
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isIdle -> IdleContent(
                    onPickFile = { launcher.launch(arrayOf("*/*")) },
                )
                uiState.isLoading -> LoadingContent()
                uiState.error != null -> ErrorContent(
                    message = uiState.error!!,
                    onRetry = { vm.resetToIdle() },
                )
                uiState.matches.isEmpty() -> EmptyContent()
                else -> MatchListContent(
                    matches = uiState.matches,
                    onConfirm = { vm.confirmMatch(it) },
                    onSkip = { vm.skipMatch(it) },
                    onCreateFromBank = { vm.createFromBank(it.bankTransaction) },
                )
            }
        }
    }
}

@Composable
private fun IdleContent(onPickFile: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.sync_idle_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onPickFile) {
            Text(stringResource(R.string.sync_pick_csv_button))
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.sync_no_matches),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.sync_error_prefix, message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry) {
            Text(stringResource(R.string.sync_retry_button))
        }
    }
}

@Composable
private fun MatchListContent(
    matches: List<TransactionMatchResult>,
    onConfirm: (TransactionMatchResult) -> Unit,
    onSkip: (TransactionMatchResult) -> Unit,
    onCreateFromBank: (TransactionMatchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        items(matches, key = { "${it.appTransaction.uid}-${it.bankTransaction.bankId}" }) { match ->
            MatchResultCard(
                match = match,
                onConfirm = { onConfirm(match) },
                onSkip = { onSkip(match) },
                onCreateFromBank = { onCreateFromBank(match) },
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

/**
 * Card showing one [TransactionMatchResult] with side-by-side app vs. bank details
 * and action buttons.
 */
@Composable
private fun MatchResultCard(
    match: TransactionMatchResult,
    onConfirm: () -> Unit,
    onSkip: () -> Unit,
    onCreateFromBank: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Confidence badge
            ConfidenceBadge(confidence = match.confidence)

            Spacer(Modifier.height(8.dp))

            // App transaction side
            Text(
                text = stringResource(R.string.sync_app_transaction),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = match.appTransaction.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${formatAmount(match.appTransaction.amount)} €  •  ${match.appTransaction.dateTime.toLocalDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            // Bank transaction side
            Text(
                text = stringResource(R.string.sync_bank_transaction),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = match.bankTransaction.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${formatAmount(match.bankTransaction.amount)} €  •  ${match.bankTransaction.valueDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedButton(onClick = onCreateFromBank) {
                    Text(stringResource(R.string.sync_import_as_new))
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onSkip) {
                    Text(stringResource(R.string.sync_skip))
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onConfirm) {
                    Text(stringResource(R.string.sync_confirm))
                }
            }
        }
    }
}

/**
 * Chip displaying the [MatchConfidence] level with a color-coded background.
 */
@Composable
private fun ConfidenceBadge(confidence: MatchConfidence, modifier: Modifier = Modifier) {
    val (label, containerColor) = when (confidence) {
        MatchConfidence.HIGH -> stringResource(R.string.sync_confidence_high) to Color(0xFF2E7D32)
        MatchConfidence.MEDIUM -> stringResource(R.string.sync_confidence_medium) to Color(0xFFF57C00)
        MatchConfidence.LOW -> stringResource(R.string.sync_confidence_low) to Color(0xFFC62828)
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
