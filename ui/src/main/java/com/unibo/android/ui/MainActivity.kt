package com.unibo.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.unibo.android.ui.navigation.AppNavigation
import com.unibo.android.ui.theme.ChronioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChronioTheme {
                AppNavigation()
            }
        }
    }
}
