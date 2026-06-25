package com.tecsup.cookplan.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MealReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CookPlanNotificationHelper.createChannels(context)

        val recipeId = intent.getLongExtra(CookPlanNotificationHelper.EXTRA_RECIPE_ID, -1L)
            .takeIf { it > 0L }
            ?: return
        val recipeName = intent.getStringExtra(CookPlanNotificationHelper.EXTRA_RECIPE_NAME)
            ?: "receta planificada"
        val mealType = intent.getStringExtra(CookPlanNotificationHelper.EXTRA_MEAL_TYPE)
            ?: "comida"

        CookPlanNotificationHelper.showMealReminder(
            context = context,
            recipeId = recipeId,
            recipeName = recipeName,
            mealType = mealType
        )
    }
}
