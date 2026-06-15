package com.tecsup.cookplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tecsup.cookplan.navigation.AppNavigation
import com.tecsup.cookplan.ui.theme.CookPlanTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            CookPlanTheme {

                AppNavigation()

            }
        }
    }
}
