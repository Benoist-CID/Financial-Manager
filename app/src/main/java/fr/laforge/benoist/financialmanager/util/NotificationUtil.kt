package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Notification

interface NotificationUtil {
    fun createNotification(notification: Notification)
}
