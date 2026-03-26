package fr.laforge.benoist.financialmanager.infrastructure.notification

/**
 * Infrastructure port for displaying a balance-update notification to the user.
 *
 * This interface lives in the infrastructure layer (not the domain) because it is
 * inherently an Android display concern — its single responsibility is to post an
 * Android system notification whenever the user's balance changes.
 *
 * It was deliberately separated from
 * [fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper],
 * which contains only pure parsing operations and must remain framework-free.
 */
interface BalanceNotifier {

    /**
     * Posts a heads-up system notification showing the updated account balance.
     *
     * @param newBalance The latest computed balance in the user's base currency.
     *
     * @note Implementations are responsible for permission checks (POST_NOTIFICATIONS)
     * and notification channel creation on API 26+.
     */
    fun showBalanceUpdate(newBalance: Float)
}
