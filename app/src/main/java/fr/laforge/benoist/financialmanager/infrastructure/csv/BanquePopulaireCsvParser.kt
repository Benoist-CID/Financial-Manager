package fr.laforge.benoist.financialmanager.infrastructure.csv

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.sync.ParseCsvBankTransactionsUseCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Parses the semicolon-delimited CSV export produced by Banque Populaire (BPCE group)
 * into a list of [BankTransaction]s.
 *
 * Expected column layout (0-indexed, first row is header):
 * ```
 * 0  Date de comptabilisation  → bookingDate
 * 1  Libelle simplifie         → description
 * 2  Libelle operation         (ignored)
 * 3  Reference                 → bankId  (unique per transaction)
 * 4  Informations complementaires (ignored)
 * 5  Type operation            (ignored — type is derived from Debit/Credit columns)
 * 6  Categorie                 (ignored)
 * 7  Sous categorie            (ignored)
 * 8  Debit                     → amount + TransactionType.Expense  (e.g. "-20,47")
 * 9  Credit                    → amount + TransactionType.Income   (e.g. "+5789,31")
 * 10 Date operation            (ignored)
 * 11 Date de valeur            → valueDate  (primary matching date)
 * 12 Pointage operation        (ignored)
 * ```
 *
 * Amount format: French locale — comma as decimal separator, optional leading +/− sign.
 * Absolute value is stored; [TransactionType] is determined by which column is non-empty.
 *
 * Rows where both Debit and Credit are blank (no monetary movement) are silently skipped.
 * Rows with fewer than [EXPECTED_COLUMN_COUNT] fields are also silently skipped so that
 * trailing blank lines or unexpected format additions do not abort the entire import.
 *
 * @note Callers are responsible for decoding the file bytes before passing the content here.
 *   ISO-8859-1 is typical for Banque Populaire exports; UTF-8 works for modern ones.
 *   Use `inputStream.bufferedReader(Charsets.ISO_8859_1).use { it.readText() }`.
 */
class BanquePopulaireCsvParser : ParseCsvBankTransactionsUseCase {

    /**
     * Parses [csvContent] line by line, skipping the header and any unparseable rows.
     *
     * @param csvContent Fully decoded CSV text.
     * @return [Result.success] with the list of parsed [BankTransaction]s (possibly empty),
     *   or [Result.failure] if an unexpected exception occurs during iteration.
     */
    override fun invoke(csvContent: String): Result<List<BankTransaction>> = runCatching {
        csvContent.lines()
            .drop(1) // skip header row
            .filter { it.isNotBlank() }
            .mapNotNull { line -> parseLine(line) }
    }

    /**
     * Parses a single CSV data row into a [BankTransaction], or returns `null` if the row
     * is malformed or has no monetary movement.
     *
     * @param line A single non-blank CSV row (header already stripped by caller).
     * @return The parsed [BankTransaction], or `null` to silently skip the row.
     */
    private fun parseLine(line: String): BankTransaction? {
        val cols = line.split(DELIMITER)
        if (cols.size < EXPECTED_COLUMN_COUNT) return null

        val debitRaw = cols[COL_DEBIT].trim()
        val creditRaw = cols[COL_CREDIT].trim()

        val (amount, type) = when {
            debitRaw.isNotEmpty() -> parseAmount(debitRaw) to TransactionType.Expense
            creditRaw.isNotEmpty() -> parseAmount(creditRaw) to TransactionType.Income
            else -> return null // no monetary movement — skip silently
        }

        return BankTransaction(
            bankId = cols[COL_REFERENCE].trim(),
            description = cols[COL_LIBELLE_SIMPLIFIE].trim(),
            bookingDate = parseDate(cols[COL_BOOKING_DATE].trim()),
            valueDate = parseDate(cols[COL_VALUE_DATE].trim()),
            amount = amount,
            type = type,
        )
    }

    /**
     * Converts a French-locale amount string (e.g. "-20,47" or "+5 789,31") to an absolute
     * [Float] value. The sign is discarded; [TransactionType] carries the direction.
     *
     * @param raw The raw cell content from the Debit or Credit column.
     * @return Absolute monetary value as a [Float].
     */
    private fun parseAmount(raw: String): Float =
        raw.trimStart('+', '-')
            .replace("\u00A0", "") // strip non-breaking spaces used as thousands separator
            .replace(' ', ' '.also { }) // strip regular space thousands separators
            .replace(',', '.')
            .trim()
            .toFloat()

    /**
     * Parses a date string in the `dd/MM/yyyy` format used by Banque Populaire.
     *
     * @param raw The raw cell content from a date column.
     * @return The corresponding [LocalDate].
     */
    private fun parseDate(raw: String): LocalDate = LocalDate.parse(raw, DATE_FORMATTER)

    private companion object {
        const val DELIMITER = ";"
        const val EXPECTED_COLUMN_COUNT = 13

        const val COL_BOOKING_DATE = 0
        const val COL_LIBELLE_SIMPLIFIE = 1
        const val COL_REFERENCE = 3
        const val COL_DEBIT = 8
        const val COL_CREDIT = 9
        const val COL_VALUE_DATE = 11

        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
