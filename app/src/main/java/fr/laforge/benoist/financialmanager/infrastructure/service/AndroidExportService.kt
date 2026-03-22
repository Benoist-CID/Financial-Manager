package fr.laforge.benoist.financialmanager.infrastructure.service

import android.content.Context
import android.content.Intent
import fr.laforge.benoist.financialmanager.presentation.util.ExportService

/**
 * Android-specific implementation of [ExportService] that uses [Intent.ACTION_SEND]
 * to share content with other apps.
 */
class AndroidExportService(private val context: Context) : ExportService {

    override fun export(content: String, subject: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            // Add flag to start activity from non-activity context if necessary
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooserIntent = Intent.createChooser(sharingIntent, "Share using").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }
}
