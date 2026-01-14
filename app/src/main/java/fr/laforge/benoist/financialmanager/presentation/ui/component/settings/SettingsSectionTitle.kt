package fr.laforge.benoist.financialmanager.presentation.ui.component.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * A composable that displays a title for a settings section.
 */
@Composable
fun SettingsSectionTitle(titleId: Int) {
    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
    )
}
