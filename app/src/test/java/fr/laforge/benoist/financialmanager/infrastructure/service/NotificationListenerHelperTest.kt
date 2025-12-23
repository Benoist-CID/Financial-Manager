package fr.laforge.benoist.financialmanager.infrastructure.service

import android.app.Notification
import android.service.notification.StatusBarNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelperTest
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be`
import org.junit.Test

class NotificationListenerHelperTest {
    private val notificationListenerHelper = NotificationListenerHelper()
    @Test
    fun `isGroupSummary returns true for group summary notifications`() {
        // --- Arrange ---
        val sbn: StatusBarNotification = mockk(relaxed = true)
        val notification: Notification = mockk(relaxed = true)
        notification.flags = Notification.FLAG_GROUP_SUMMARY
        every { sbn.notification } returns notification

        // --- Act ---
        val result = notificationListenerHelper.isGroupSummary(sbn)

        // --- Assert ---
        result `should be` true
    }

    @Test
    fun `isGroupSummary returns false for non-group summary notifications`() {
        // --- Arrange ---
        val sbn: StatusBarNotification = mockk(relaxed = true)
        val notification: Notification = mockk(relaxed = true)
        notification.flags = Notification.FLAG_LOCAL_ONLY
        every { sbn.notification } returns notification

        // --- Act ---
        val result = notificationListenerHelper.isGroupSummary(sbn)

        // --- Assert ---
        result `should be` false
    }
}
