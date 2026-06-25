package com.tecsup.cookplan.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.cookplan.MainActivity
import com.tecsup.cookplan.R

object CookPlanNotificationHelper {
    const val CHANNEL_MEAL_REMINDERS = "meal_reminders"
    const val EXTRA_RECIPE_ID = "extra_recipe_id"
    const val EXTRA_RECIPE_NAME = "extra_recipe_name"
    const val EXTRA_MEAL_TYPE = "extra_meal_type"
    const val DEFAULT_REMINDER_DELAY_MILLIS = 10_000L

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_MEAL_REMINDERS,
            "Recordatorios de comidas",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos para las comidas planificadas en CookPlan"
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun showMealReminder(
        context: Context,
        recipeId: Long,
        recipeName: String,
        mealType: String
    ) {
        if (!canPostNotifications(context)) return

        val pendingIntent = recipePendingIntent(context, recipeId)
        val notification = NotificationCompat.Builder(context, CHANNEL_MEAL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Recordatorio de $mealType")
            .setContentText("Toca para ver la receta: $recipeName")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context)
            .notify(("meal-$recipeId-$mealType").hashCode(), notification)
    }

    fun scheduleMealReminder(
        context: Context,
        recipeId: Long,
        recipeName: String,
        mealType: String,
        delayMillis: Long = DEFAULT_REMINDER_DELAY_MILLIS
    ) {
        val intent = Intent(context, MealReminderReceiver::class.java).apply {
            putExtra(EXTRA_RECIPE_ID, recipeId)
            putExtra(EXTRA_RECIPE_NAME, recipeName)
            putExtra(EXTRA_MEAL_TYPE, mealType)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ("meal-$recipeId-$mealType").hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            pendingIntent
        )
    }

    fun scheduleMealReminderAt(
        context: Context,
        recipeId: Long,
        recipeName: String,
        mealType: String,
        triggerAtMillis: Long
    ) {
        val delayMillis = (triggerAtMillis - System.currentTimeMillis()).coerceAtLeast(1_000L)
        scheduleMealReminder(
            context = context,
            recipeId = recipeId,
            recipeName = recipeName,
            mealType = mealType,
            delayMillis = delayMillis
        )
    }

    fun showPushNotification(
        context: Context,
        title: String,
        body: String,
        recipeId: Long? = null
    ) {
        if (!canPostNotifications(context)) return

        val pendingIntent = recipePendingIntent(context, recipeId)
        val notification = NotificationCompat.Builder(context, CHANNEL_MEAL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context)
            .notify(("push-${System.currentTimeMillis()}").hashCode(), notification)
    }

    private fun recipePendingIntent(context: Context, recipeId: Long?): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            recipeId?.let { putExtra(EXTRA_RECIPE_ID, it) }
        }

        return PendingIntent.getActivity(
            context,
            recipeId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }
}
