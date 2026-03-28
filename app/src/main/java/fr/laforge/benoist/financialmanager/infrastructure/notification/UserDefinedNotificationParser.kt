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
 *   Commas are normalised to dots before parsing. The amount may be negative
 *   either because the body contains a leading `-` (e.g. `-10,50€`) or because
 *   the pattern itself prefixes `{amount}` with a `-` sign (see below).
 * - `{description}` — the substring at this position is used as the transaction
 *   description. Relevant only when [NotificationFormat.descriptionSource] is
 *   [DescriptionSource.BODY]. If absent and source is [DescriptionSource.BODY],
 *   the title is used as a fallback.
 * - All other text is treated as a **literal separator** that must be present in
 *   [body] for the parser to match.
 *
 * ## Sign prefix (`-{amount}`)
 * If the literal separator immediately before `{amount}` ends with `-`, the `-`
 * acts as a **sign indicator**: it is still matched as a literal (so the body must
 * contain it), but the extracted numeric value is negated in the result.
 *
 * Example — pattern `"-{amount}€, {description}"` + body `"-10,50€, Amazon"`:
 * - the `-` literal is matched and consumed;
 * - amount extracted → `"10,50"` → negated → `-10.5f`;
 * - description extracted → `"Amazon"`.
 *
 * Amounts that are inherently negative in the body (e.g. body `"-10,50€"` with
 * pattern `"{amount}€"`) are also accepted without a sign prefix.
 *
 * ## Description source ([NotificationFormat.descriptionSource])
 * - [DescriptionSource.BODY]: description is extracted from [body] via `{description}`,
 *   or from [title] as a fallback when the placeholder is absent.
 * - [DescriptionSource.TITLE]: [title] is always used as the description;
 *   any `{description}` placeholder in the pattern is ignored.
 *
 * @property format The user-defined format to use for parsing.
 *
 * @implNote The pattern is compiled once into an ordered list of [Segment]s so that
 *   [canParse] and [parse] share the same structural analysis without duplicating logic.
 *   A post-processing pass over the compiled segments detects the `-` sign prefix and
 *   marks the [Segment.Amount] accordingly.
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

        val amount = extracted.amountStr.replace(',', '.').toFloatOrNull()?.let { kotlin.math.abs(it) }
            ?: return Result.failure(
                IllegalArgumentException("Cannot parse amount '${extracted.amountStr}' in: $body")
            )

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
        /**
         * @property negate When `true`, the extracted numeric value is negated.
         *   Set by [compilePattern] when the preceding literal ends with `-`.
         */
        data class Amount(val negate: Boolean = false) : Segment
        data object Description : Segment
    }

    /**
     * Turns the pattern string into an ordered list of [Segment]s, then applies a
     * post-processing pass to detect the `-` sign prefix before `{amount}`.
     *
     * E.g. `"-{amount} € {description}"` →
     *   `[Literal("-"), Amount(negate=true), Literal(" € "), Description]`
     */
    private fun compilePattern(pattern: String): List<Segment> {
        val raw = buildRawSegments(pattern)
        return applySignPrefix(raw)
    }

    /**
     * First pass: splits the pattern string into raw [Segment]s without sign analysis.
     */
    private fun buildRawSegments(pattern: String): List<Segment> {
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
            segments += if (ph == AMOUNT_PLACEHOLDER) Segment.Amount() else Segment.Description
            remaining = remaining.substring(idx + ph.length)
        }
        return segments
    }

    /**
     * Second pass: if a [Segment.Literal] whose text ends with `'-'` is immediately
     * followed by a [Segment.Amount], marks that amount as negated.
     *
     * The literal itself is kept unchanged so that the `-` in the notification body
     * is still consumed during extraction — only the final sign of the result changes.
     *
     * @implNote This design means the body **must** contain the `-` for the pattern to
     *   match. A body without the leading `-` will not be recognised by [canParse].
     */
    private fun applySignPrefix(segments: List<Segment>): List<Segment> {
        val result = mutableListOf<Segment>()
        for (i in segments.indices) {
            val current = segments[i]
            val next = segments.getOrNull(i + 1)
            if (current is Segment.Literal &&
                current.text.endsWith("-") &&
                next is Segment.Amount
            ) {
                result += current
                result += Segment.Amount(negate = true)
            } else if (current is Segment.Amount && i > 0) {
                val prev = segments[i - 1]
                // Skip: already emitted as Amount(negate=true) in the previous iteration
                if (prev is Segment.Literal && prev.text.endsWith("-")) continue
                result += current
            } else {
                result += current
            }
        }
        return result
    }

    private data class Extracted(
        val amountStr: String,
        val description: String?,
        /** `true` when the preceding literal's sign prefix requested negation. */
        val negate: Boolean = false,
    )

    /**
     * Attempts to extract amount and description from [body] using [segments].
     *
     * Returns `null` if the body does not conform to the pattern.
     */
    private fun extract(body: String): Extracted? {
        var cursor = 0
        var amountStr: String? = null
        var amountNegate = false
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
                    amountNegate = segment.negate
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

        return amountStr?.let { Extracted(it, description, amountNegate) }
    }

    companion object {
        const val AMOUNT_PLACEHOLDER = "{amount}"
        const val DESCRIPTION_PLACEHOLDER = "{description}"
    }
}
