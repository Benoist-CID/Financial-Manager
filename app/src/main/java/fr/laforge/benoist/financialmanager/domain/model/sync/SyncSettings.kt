package fr.laforge.benoist.financialmanager.domain.model.sync

/**
 * User-configurable settings that govern the transaction reconciliation algorithm.
 *
 * Stored independently of general app preferences because sync settings are expected
 * to grow as the feature matures (e.g. per-account date-window overrides, confidence
 * thresholds) and mixing them with general preferences would couple unrelated concerns.
 *
 * @property recurringAmountTolerancePercent The maximum allowed percentage difference
 *   between a recurring app transaction amount and a bank transaction amount for the
 *   two to be considered a fuzzy-amount match.
 *   Example: a value of `1f` means amounts within 1% of each other will match.
 *
 * @note The default of 1% was chosen to absorb typical rounding differences (e.g. a
 *   €9.99 subscription appearing as €10.00) while being strict enough to avoid false
 *   positives between subscriptions with similar prices.
 */
data class SyncSettings(
    val recurringAmountTolerancePercent: Float = 1f,
)
