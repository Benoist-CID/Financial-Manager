package fr.laforge.benoist.financialmanager.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.laforge.benoist.financialmanager.MainActivity
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.model.Notification

class AndroidNotificationUtil(private val context: Context) : NotificationUtil {
    override fun createNotification(notification: Notification) {
        val channelId = "my_channel_id"
        val channelName = "My Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            channelId, channelName,
            importance
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


        // Créer les PendingIntents pour les actions
        val intentYes = Intent(context, MainActivity::class.java).apply {
            putExtra("action", notification.message)
        }
        val pendingIntentYes = PendingIntent.getActivity(
            context,
            0,
            intentYes,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentNo = Intent(context, MainActivity::class.java).apply {
            putExtra("action", "no")
        }
        val pendingIntentNo = PendingIntent.getActivity(
            context,
            1,
            intentNo,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construire la notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(notification.message)
            .setSmallIcon(R.drawable.euro_symbol)
            .addAction(R.drawable.telecom, "Oui", pendingIntentYes)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Afficher la notification
        with(NotificationManagerCompat.from(context)) {
            val notificationId = 1
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }
}
