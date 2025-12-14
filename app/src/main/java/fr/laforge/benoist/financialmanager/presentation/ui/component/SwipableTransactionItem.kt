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
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipableTransactionItem(
    transaction: Transaction, // Make sure to import your Transaction model
    isPeriodic: Boolean,
    onDelete: (Boolean) -> Unit, // Boolean indicates "shouldDeleteParent"
    onClick: () -> Unit
) {
    val dismissState = rememberDismissState()
    val openAlertDialog = remember { mutableStateOf(false) }
    val openPeriodicAlertDialog = remember { mutableStateOf(false) }

    // We use a CoroutineScope bound to this UI component for animations
    val scope = rememberCoroutineScope()

    // Logic to detect swipe and show specific dialogs
    if (dismissState.isDismissed(direction = DismissDirection.EndToStart)) {
        // We defer the state update to a LaunchedEffect to avoid side-effects during composition
        LaunchedEffect(Unit) {
            if (isPeriodic) {
                openPeriodicAlertDialog.value = true
            } else {
                openAlertDialog.value = true
            }
            // Snap back immediately; actual removal happens after dialog confirmation
            dismissState.snapTo(DismissValue.Default)
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
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val backgroundColor by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.6f)
                    else -> Color.White
                }, label = ""
            )
            val iconScale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 1.3f else 0.5f,
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
                    tint = Color.White,
                )
            }
        },
        dismissContent = {
            TransactionRow(transaction) { onClick() }
        },
    )
}
