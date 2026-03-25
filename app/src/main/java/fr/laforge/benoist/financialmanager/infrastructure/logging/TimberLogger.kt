package fr.laforge.benoist.financialmanager.infrastructure.logging

import fr.laforge.benoist.financialmanager.domain.util.Logger
import timber.log.Timber

/**
 * Infrastructure implementation of [Logger] backed by Timber.
 *
 * Timber is an Android-specific logging library and must never be imported
 * in the domain layer. This class acts as the boundary adapter, translating
 * domain-level log calls into the appropriate Timber equivalents.
 *
 * @note Timber must be planted (via [Timber.plant]) before this logger is
 * used. Planting is performed in [FinancialManagerApp] at application startup.
 */
class TimberLogger : Logger {

    /**
     * Delegates to [Timber.d] for verbose diagnostic output.
     *
     * @param message The message to log.
     */
    override fun debug(message: String) {
        Timber.d(message)
    }

    /**
     * Delegates to [Timber.i] for significant lifecycle events.
     *
     * @param message The message to log.
     */
    override fun info(message: String) {
        Timber.i(message)
    }

    /**
     * Delegates to [Timber.e] for error-level output.
     *
     * @param message A human-readable description of the error.
     * @param throwable The exception that caused the error, if available.
     */
    override fun error(message: String, throwable: Throwable?) {
        Timber.e(throwable, message)
    }
}
