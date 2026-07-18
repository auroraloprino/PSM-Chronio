package com.unibo.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unibo.android.ui.navigation.AppNavigation
import com.unibo.android.ui.theme.ChronioTheme
import com.unibo.android.ui.theme.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themePreference = ThemePreference(this)
        setContent {
            var isDark by remember { mutableStateOf(themePreference.isDark) }
            ChronioTheme(darkTheme = isDark) {
                AppNavigation(
                    onToggleTheme = {
                        isDark = !isDark
                        themePreference.isDark = isDark
                    },
                    isDark = isDark
                )
            }
        }
    }
}
