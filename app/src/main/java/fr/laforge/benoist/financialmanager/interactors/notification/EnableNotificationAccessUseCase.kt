package fr.laforge.benoist.financialmanager.interactors.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils

interface EnableNotificationAccessUseCase {
    /**
     * Checks if the notification acess right has been granted, and if not, displays settings
     */
    operator fun invoke()
}

class EnableNotificationAccessUseCaseImpl(private var context: Context) :
    EnableNotificationAccessUseCase {
    override fun invoke() {
        if (!isNotificationServiceEnabled(context = context)) {
            val intent = Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent(intent))
        }
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     *
     * @param context Context
     *
     * @return True if enabled, false otherwise.
     */
    private fun isNotificationServiceEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat: String = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
