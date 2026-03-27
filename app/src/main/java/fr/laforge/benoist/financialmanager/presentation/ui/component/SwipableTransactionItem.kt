package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.presentation.ui.theme.BalanceNegativeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipableTransactionItem(
    transaction: Transaction,
    isPeriodic: Boolean,
    onDelete: (Boolean) -> Unit, // Boolean indicates "shouldDeleteParent"
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val openAlertDialog = remember { mutableStateOf(false) }
    val openPeriodicAlertDialog = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Detect swipe completion and show the appropriate dialog
    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        LaunchedEffect(Unit) {
            if (isPeriodic) {
                openPeriodicAlertDialog.value = true
            } else {
                openAlertDialog.value = true
            }
            // Snap back immediately; actual removal happens after dialog confirmation
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    // --- Periodic Delete Dialog ---
    if (openPeriodicAlertDialog.value) {
        ShowDialog(
            onDismissRequest = { openPeriodicAlertDialog.value = false },
            onConfirmation = {
                onDelete(true) // Delete parent
                openPeriodicAlertDialog.value = false
            },
            onThirdButton = {
                onDelete(false) // Just this one
                openPeriodicAlertDialog.value = false
            },
            dialogTitle = stringResource(R.string.periodic_transaction),
            dialogText = stringResource(R.string.periodic_transaction_description),
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            confirmButtonText = stringResource(R.string.all),
            dismissButtonText = stringResource(R.string.cancel),
            thirdButtonText = stringResource(R.string.just_this_one),
            dialogType = DialogType.ThreeButtons
        )
    }

    // --- Standard Delete Dialog ---
    if (openAlertDialog.value) {
        ShowDialog(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = {
                onDelete(false)
                openAlertDialog.value = false
            },
            dialogTitle = stringResource(R.string.do_you_want_to_delete),
            dialogText = stringResource(R.string.delete_warning_message),
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
        )
    }

    // --- Swipe Component ---
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> BalanceNegativeRed.copy(alpha = 0.6f)
                    else -> MaterialTheme.colorScheme.surface
                }, label = ""
            )
            val iconScale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.3f else 0.5f,
                label = ""
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    modifier = Modifier.scale(iconScale),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError,
                )
            }
        },
        content = {
            TransactionRow(transaction) { onClick() }
        },
    )
}
