package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.DescriptionSource
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationParser

/**
 * A [NotificationParser] driven by a single user-defined [NotificationFormat].
 *
 * ## Pattern syntax
 * The [NotificationFormat.pattern] uses two reserved placeholders:
 * - `{amount}` — the substring at this position is parsed as a [Float].
 *   Commas are normalised to dots before parsing.
 * - `{description}` — the substring at this position is used as the transaction description.
 *   Relevant only when [NotificationFormat.descriptionSource] is [DescriptionSource.BODY].
 *   If the placeholder is absent and source is [DescriptionSource.BODY], the title is used.
 * - All other text is treated as a **literal separator** that must be present in [body]
 *   for the parser to match.
 *
 * ## Description source ([NotificationFormat.descriptionSource])
 * - [DescriptionSource.BODY]: description is extracted from [body] via `{description}`,
 *   or from [title] as a fallback when the placeholder is absent.
 * - [DescriptionSource.TITLE]: [title] is always used as the description;
 *   any `{description}` placeholder in the pattern is ignored.
 *
 * ## Detection ([canParse])
 * Returns `true` when all literal separators derived from the pattern are found in [body]
 * in the correct order, AND the text at the `{amount}` position is numeric.
 *
 * ## Example
 * Pattern `"{amount} € {description}"` matches body `"10,50 € Amazon Prime"`:
 * - separator between `{amount}` and `{description}` → `" € "`
 * - amount extracted → `"10,50"` → `10.5f`
 * - description extracted → `"Amazon Prime"`
 *
 * @property format The user-defined format to use for parsing.
 *
 * @implNote The pattern is compiled once into an ordered list of [Segment]s so that
 *   [canParse] and [parse] share the same structural analysis without duplicating logic.
 */
class UserDefinedNotificationParser(
    private val format: NotificationFormat,
) : NotificationParser {

    /**
     * Compiled representation of the pattern — evaluated lazily once per instance.
     */
    private val segments: List<Segment> by lazy { compilePattern(format.pattern) }

    override fun canParse(body: String): Boolean = extract(body) != null

    override fun parse(title: String, body: String): Result<ParsedNotification> {
        val extracted = extract(body)
            ?: return Result.failure(
                IllegalArgumentException("Body does not match pattern '${format.pattern}': $body")
            )

        val amount = extracted.amountStr.replace(',', '.').toFloatOrNull()
            ?: return Result.failure(
                IllegalArgumentException("Cannot parse amount '${extracted.amountStr}' in: $body")
            )

        if (amount < 0) {
            return Result.failure(IllegalArgumentException("Negative amount: $amount"))
        }

        val description = when (format.descriptionSource) {
            DescriptionSource.TITLE -> title
            DescriptionSource.BODY -> extracted.description ?: title
        }

        return Result.success(
            ParsedNotification(
                amount = amount,
                description = description,
                source = NotificationSource.CUSTOM,
            )
        )
    }

    // -------------------------------------------------------------------------
    // Private implementation
    // -------------------------------------------------------------------------

    private sealed interface Segment {
        data class Literal(val text: String) : Segment
        data object Amount : Segment
        data object Description : Segment
    }

    /**
     * Turns the pattern string into an ordered list of [Segment]s.
     *
     * E.g. `"{amount} € {description}"` → `[Amount, Literal(" € "), Description]`
     */
    private fun compilePattern(pattern: String): List<Segment> {
        val segments = mutableListOf<Segment>()
        var remaining = pattern
        while (remaining.isNotEmpty()) {
            val nextPlaceholder = listOf(AMOUNT_PLACEHOLDER, DESCRIPTION_PLACEHOLDER)
                .mapNotNull { ph -> remaining.indexOf(ph).takeIf { it >= 0 }?.let { it to ph } }
                .minByOrNull { it.first }

            if (nextPlaceholder == null) {
                if (remaining.isNotBlank()) segments += Segment.Literal(remaining)
                break
            }

            val (idx, ph) = nextPlaceholder
            if (idx > 0) segments += Segment.Literal(remaining.substring(0, idx))
            segments += if (ph == AMOUNT_PLACEHOLDER) Segment.Amount else Segment.Description
            remaining = remaining.substring(idx + ph.length)
        }
        return segments
    }

    private data class Extracted(val amountStr: String, val description: String?)

    /**
     * Attempts to extract amount and description from [body] using [segments].
     *
     * Returns `null` if the body does not conform to the pattern.
     */
    private fun extract(body: String): Extracted? {
        var cursor = 0
        var amountStr: String? = null
        var description: String? = null

        for (i in segments.indices) {
            val segment = segments[i]
            val nextLiteral = segments.drop(i + 1).filterIsInstance<Segment.Literal>().firstOrNull()?.text

            when (segment) {
                is Segment.Literal -> {
                    val pos = body.indexOf(segment.text, cursor)
                    if (pos < 0) return null
                    cursor = pos + segment.text.length
                }
                is Segment.Amount -> {
                    val end = if (nextLiteral != null) body.indexOf(nextLiteral, cursor) else body.length
                    if (end < 0) return null
                    val candidate = body.substring(cursor, end).trim()
                    if (candidate.replace(',', '.').toFloatOrNull() == null) return null
                    amountStr = candidate
                    cursor = end
                }
                is Segment.Description -> {
                    val end = if (nextLiteral != null) body.indexOf(nextLiteral, cursor) else body.length
                    if (end < 0) return null
                    description = body.substring(cursor, end).trim()
                    cursor = end
                }
            }
        }

        return amountStr?.let { Extracted(it, description) }
    }

    companion object {
        const val AMOUNT_PLACEHOLDER = "{amount}"
        const val DESCRIPTION_PLACEHOLDER = "{description}"
    }
}
