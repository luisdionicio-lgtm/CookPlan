package com.tecsup.cookplan.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CookPlanMessagingService : FirebaseMessagingService() {
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
