package fr.laforge.benoist.financialmanager.domain.model.indicator

enum class LifestyleStatus(val maxThreshold: Float) {
    Excellent(0.30f), // Up to 30%
    Healthy(0.50f),   // Up to 50%
    Heavy(0.75f),     // Up to 75%
    Danger(1.00f);    // Above 75%

    companion object {
        /**
         * Returns the correct status for a given ratio.
         */
        fun from(ratio: Float): LifestyleStatus {
            return entries.firstOrNull { ratio <= it.maxThreshold } ?: Danger
        }
    }
}
