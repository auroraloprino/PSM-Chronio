package com.unibo.android.ui.theme

import android.content.Context

class ThemePreference(context: Context) {
    private val prefs = context.getSharedPreferences("chronio_prefs", Context.MODE_PRIVATE)

    var isDark: Boolean
        get() = prefs.getBoolean("dark_theme", false)
        set(value) = prefs.edit().putBoolean("dark_theme", value).apply()
}
