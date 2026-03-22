package fr.laforge.benoist.financialmanager.presentation.util

/**
 * Interface for exporting content to external services (e.g., sharing, saving to file).
 */
interface ExportService {

    /**
     * Exports the given content.
     *
     * @param content The literal text content to export.
     * @param subject The subject or title for the export operation.
     */
    fun export(content: String, subject: String)
}
