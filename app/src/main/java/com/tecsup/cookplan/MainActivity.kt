package com.tecsup.cookplan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.tecsup.cookplan.navigation.AppNavigation
import com.tecsup.cookplan.notifications.CookPlanNotificationHelper
import com.tecsup.cookplan.ui.theme.CookPlanTheme

class MainActivity : ComponentActivity() {
    private var notificationRecipeId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CookPlanNotificationHelper.createChannels(this)
        notificationRecipeId = intent.recipeIdExtra()

        setContent {

            CookPlanTheme {
                NotificationPermissionEffect()

                AppNavigation(
                    notificationRecipeId = notificationRecipeId,
                    onNotificationHandled = { notificationRecipeId = null }
                )

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationRecipeId = intent.recipeIdExtra()
    }

    private fun Intent?.recipeIdExtra(): Long? {
        val value = this?.getLongExtra(CookPlanNotificationHelper.EXTRA_RECIPE_ID, -1L) ?: -1L
        return value.takeIf { it > 0L }
    }
}

@androidx.compose.runtime.Composable
private fun NotificationPermissionEffect() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
