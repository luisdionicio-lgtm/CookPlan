package com.tecsup.cookplan

import android.app.NotificationManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.cookplan.notifications.CookPlanMessagingService
import com.tecsup.cookplan.notifications.CookPlanNotificationHelper
import com.tecsup.cookplan.notifications.MealReminderReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Parte2IntegrationTest {
    private val context = ApplicationProvider.getApplicationContext<CookPlanApplication>()

    @Test
    fun notificationChannel_isCreatedForMealReminders() {
        CookPlanNotificationHelper.createChannels(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = manager.getNotificationChannel(
                CookPlanNotificationHelper.CHANNEL_MEAL_REMINDERS
            )

            assertNotNull(channel)
            assertEquals("Recordatorios de comidas", channel.name.toString())
        } else {
            assertTrue(true)
        }
    }

    @Test
    fun manifest_declaresFcmServiceAndMealReminderReceiver() {
        val packageManager = context.packageManager

        val serviceInfo = packageManager.getServiceInfo(
            ComponentName(context, CookPlanMessagingService::class.java),
            0
        )
        val receiverInfo = packageManager.getReceiverInfo(
            ComponentName(context, MealReminderReceiver::class.java),
            0
        )

        assertEquals(false, serviceInfo.exported)
        assertEquals(false, receiverInfo.exported)
    }

    @Test
    fun firebaseRepositories_areAvailableFromApplicationContainer() {
        assertNotNull(context.authRepository)
        assertNotNull(context.recipeRepository)
        assertNotNull(context.mealPlanRepository)
        assertNotNull(context.syncRepository)
    }
}
