package fr.laforge.benoist.financialmanager.infrastructure.service

import android.app.Notification
import android.service.notification.StatusBarNotification

/**
 * Helper class for NotificationListenerService
 */
class NotificationListenerHelper {
    /**
     * Checks if a notification is a group summary notification
     *
     * @param sbn StatusBarNotification
     *
     * @return Boolean
     */
    fun isGroupSummary(sbn: StatusBarNotification): Boolean =
        (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
}
