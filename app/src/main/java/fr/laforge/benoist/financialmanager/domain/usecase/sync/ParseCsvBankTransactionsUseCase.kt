package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction

/**
 * Domain port for parsing a raw bank-exported CSV string into [BankTransaction]s.
 *
 * Lives in the domain layer so that callers (ViewModels, tests) can depend on this
 * abstraction rather than on a concrete parser. The encoding/reading of the source file
 * is the caller's responsibility; this use case receives already-decoded text.
 *
 * Implementations are expected to:
 * - Skip the header row.
 * - Silently skip any row that cannot be parsed (malformed, blank, or missing amount).
 * - Return [Result.failure] only for catastrophic failures (e.g. completely unrecognised format).
 */
interface ParseCsvBankTransactionsUseCase {

    /**
     * Parses [csvContent] and returns all bank transactions found.
     *
     * @param csvContent Raw CSV text decoded to a [String]. File encoding is the caller's
     *   responsibility — Banque Populaire typically exports ISO-8859-1.
     * @return [Result.success] with the parsed list (may be empty if the file contains only
     *   a header or all rows are blank), or [Result.failure] if parsing fails entirely.
     */
    operator fun invoke(csvContent: String): Result<List<BankTransaction>>
}
