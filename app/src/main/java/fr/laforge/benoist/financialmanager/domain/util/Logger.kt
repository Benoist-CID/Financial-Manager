package fr.laforge.benoist.financialmanager.domain.util

/**
 * Domain-level logging abstraction.
 *
 * The domain layer must not depend on any framework-specific logging solution
 * (e.g. Timber, Log). This interface allows use cases to emit diagnostic
 * messages without coupling the domain to an Android library.
 *
 * @note Implementations live in the infrastructure layer (e.g. TimberLogger).
 * The [NoOp] singleton is the safe default for tests and any context where
 * logging is not required — callers never need to null-check the logger.
 */
interface Logger {

    /**
     * Emits a debug-level message, intended for verbose diagnostic output.
     *
     * @param message The message to log.
     */
    fun debug(message: String)

    /**
     * Emits an info-level message, intended for significant lifecycle events.
     *
     * @param message The message to log.
     */
    fun info(message: String)

    /**
     * Emits an error-level message, optionally associating a [Throwable].
     *
     * @param message A human-readable description of the error.
     * @param throwable The exception that caused the error, if available.
     */
    fun error(message: String, throwable: Throwable? = null)

    /**
     * A no-operation [Logger] implementation.
     *
     * Used as the default value in use case constructors so that callers
     * (especially tests) are never forced to provide a logger explicitly.
     * All methods discard their arguments silently.
     */
    object NoOp : Logger {
        override fun debug(message: String) = Unit
        override fun info(message: String) = Unit
        override fun error(message: String, throwable: Throwable?) = Unit
    }
}
