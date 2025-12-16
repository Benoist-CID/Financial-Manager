package fr.laforge.benoist.financialmanager.domain.model.indicator

/**
 * Represents a point on a daily balance chart.
 */
data class DailyPoint(val dayOfMonth: Int, val balance: Float)
