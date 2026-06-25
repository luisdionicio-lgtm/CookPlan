package com.tecsup.cookplan.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CookPlanMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token nuevo/renovado: también lo imprimimos por si cambia.
        Log.d("FCM_TOKEN", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        CookPlanNotificationHelper.createChannels(this)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "CookPlan"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Tienes una nueva notificación."
        val recipeId = message.data["recipeId"]?.toLongOrNull()

        CookPlanNotificationHelper.showPushNotification(
            context = this,
            title = title,
            body = body,
            recipeId = recipeId
        )
    }
}
