package fr.laforge.benoist.financialmanager.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

enum class DialogType {
    TwoButtons,
    ThreeButtons,
}

@Composable
fun ShowDialog(

    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onThirdButton: () -> Unit = {},
    dialogTitle: String,
    dialogText: String,
    dialogType: DialogType = DialogType.TwoButtons,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Dismiss",
    thirdButtonText: String = "Third",
    icon: @Composable () -> Unit,
) {
    AlertDialog(
        icon = icon,
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {

            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmButtonText)
            }

            if (dialogType == DialogType.ThreeButtons) {
                TextButton(
                    onClick = {
                        onThirdButton()
                    }
                ) {
                    Text(thirdButtonText)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissButtonText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ShowDialogWith2ButtonsPreview() {
    ShowDialog(
        onDismissRequest = { /*TODO*/ },
        onConfirmation = { /*TODO*/ },
        onThirdButton = { /*TODO*/ },
        dialogTitle = "Dialog Title",
        dialogText = "This is the dialog text.",
        confirmButtonText = "Confirm",
        dismissButtonText = "Dismiss",
        icon = { Icon(Icons.Filled.Info, contentDescription = "Info") }
    )
}

@Preview(showBackground = true)
@Composable
fun ShowDialogWith3ButtonsPreview() {
    ShowDialog(
        onDismissRequest = { /*TODO*/ },
        onConfirmation = { /*TODO*/ },
        onThirdButton = { /*TODO*/ },
        dialogTitle = "Dialog Title",
        dialogText = "This is the dialog text.",
        confirmButtonText = "Toutes",
        dismissButtonText = "Annuler",
        thirdButtonText = "Celle-ci",
        dialogType = DialogType.ThreeButtons,
        icon = { Icon(Icons.Filled.Info, contentDescription = "Info") }
    )
}
