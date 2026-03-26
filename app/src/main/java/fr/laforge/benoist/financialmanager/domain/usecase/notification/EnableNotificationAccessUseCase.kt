package fr.laforge.benoist.financialmanager.domain.usecase.notification

/**
 * Application-service port for ensuring notification listener access is granted.
 *
 * This interface sits at the domain boundary as a driven port: it is called by the
 * presentation layer (e.g. [MainActivityViewModel]) and implemented in the
 * infrastructure layer ([EnableNotificationAccessUseCaseImpl]) where the Android
 * Settings navigation actually happens.
 *
 * @note Intentionally placed here rather than in the domain business-rule layer because
 * "navigate to Android Settings if permission is missing" is a system/navigation concern,
 * not a business invariant. The interface itself remains framework-free; only the
 * implementation touches Android APIs.
 */
interface EnableNotificationAccessUseCase {

    /**
     * Checks whether notification-listener access has been granted and, if not,
     * opens the system Settings screen to let the user enable it.
     */
    operator fun invoke()
}
