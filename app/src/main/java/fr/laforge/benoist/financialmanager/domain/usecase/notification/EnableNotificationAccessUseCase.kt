package fr.laforge.benoist.financialmanager.domain.usecase.notification

interface EnableNotificationAccessUseCase {
    /**
     * Checks if the notification acess right has been granted, and if not, displays settings
     */
    operator fun invoke()
}
