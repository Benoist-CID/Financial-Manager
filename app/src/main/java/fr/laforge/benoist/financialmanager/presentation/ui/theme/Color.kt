package fr.laforge.benoist.financialmanager.presentation.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Green500 = Color(0xFF1EB980)
val DarkBlue900 = Color(0xFF26282F)

// ── App-level semantic colors ─────────────────────────────────────────────────

/** Primary accent green used for the TransactionsListScreen TopAppBar. */
val AppGreen = Color(0xFF00C853)

// ── Indicator / category bar colors ─────────────────────────────────────────

/** Color for the recurring-income indicator bar. */
val IncomeGreen = Color(0xFF4CAF50)

/** Color for the variable-income indicator bar. */
val VariableIncomeGreen = Color(0xFF00E676)

/** Color for the recurring-expenses indicator bar. */
val ExpenseOrange = Color(0xFFFF9800)

/** Color for the variable-expenses indicator bar. */
val ExpenseRed = Color(0xFFF44336)

/** Color for the total-expenses summary label. */
val TotalExpenseRed = Color(0xFFD32F2F)

// ── Balance / forecast state colors ──────────────────────────────────────────

/** Text/graph color when the current balance is positive. */
val BalancePositiveBlue = Color(0xFF2196F3)

/** Text/graph color when the current balance is negative. */
val BalanceNegativeRed = Color(0xFFEF5350)

/** Dark card surface used by BalanceHeroCard. */
val DarkSurface = Color(0xFF1E1E1E)

/** ForecastCard background color when the projected balance is positive. */
val ForecastPositiveBackground = Color(0xFFE8F5E9)

/** ForecastCard content/text color when the projected balance is positive. */
val ForecastPositiveContent = Color(0xFF2E7D32)

/** ForecastCard background color when the projected balance is negative. */
val ForecastNegativeBackground = Color(0xFFFFEBEE)

/** ForecastCard content/text color when the projected balance is negative. */
val ForecastNegativeContent = Color(0xFFC62828)

// ── Lifestyle-ratio indicator colors ────────────────────────────────────────

/** Status color for the Excellent (0–30 %) lifestyle tier. */
val LifestyleExcellent = Color(0xFF66BB6A)

/** Status color for the Healthy (30–50 %) lifestyle tier. */
val LifestyleHealthy = Color(0xFF9CCC65)

/** Status color for the Heavy (50–75 %) lifestyle tier. */
val LifestyleHeavy = Color(0xFFFFCA28)

/** Status color for the Danger (75 %+) lifestyle tier. */
val LifestyleDanger = Color(0xFFEF5350)

/** Track background color for the lifestyle-ratio progress bar. */
val ProgressTrackBackground = Color(0xFF2C2C2C)
