package com.example.ui.theme

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    // Default theme color (White triggers the Black and White theme)
    private val defaultColor = Color.White

    private val _themeColor = MutableStateFlow<Color>(defaultColor)
    val themeColor: StateFlow<Color> = _themeColor.asStateFlow()

    private val _isDynamicTheme = MutableStateFlow<Boolean>(true)
    val isDynamicTheme: StateFlow<Boolean> = _isDynamicTheme.asStateFlow()

    private val _backgroundImageUri = MutableStateFlow<Uri?>(null)
    val backgroundImageUri: StateFlow<Uri?> = _backgroundImageUri.asStateFlow()

    fun setDynamicTheme(enabled: Boolean, context: Context) {
        _isDynamicTheme.value = enabled
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_dynamic_theme", enabled).apply()
    }

    fun setThemeColor(color: Color, context: Context) {
        _themeColor.value = color
        _isDynamicTheme.value = false
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("is_dynamic_theme", false)
            .putInt("theme_color", color.value.toLong().toInt())
            .apply()
    }

    fun setBackgroundImage(uri: Uri?, context: Context) {
        _backgroundImageUri.value = uri
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        if (uri == null) {
            prefs.edit().putString("bg_image", "none").apply()
        } else {
            prefs.edit().putString("bg_image", uri.toString()).apply()
        }
    }

    fun loadFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        _isDynamicTheme.value = prefs.getBoolean("is_dynamic_theme", true)
        if (prefs.contains("theme_color")) {
            val colorInt = prefs.getInt("theme_color", defaultColor.value.toLong().toInt())
            _themeColor.value = Color(colorInt)
        }
        
        if (prefs.contains("bg_image")) {
            val uriStr = prefs.getString("bg_image", "none")
            if (uriStr == "none") {
                _backgroundImageUri.value = null
            } else if (uriStr != null) {
                _backgroundImageUri.value = Uri.parse(uriStr)
            }
        }
    }
}
