cat << 'INNER_EOF' > app/src/main/java/com/example/ui/theme/ThemeManager.kt
package com.example.ui.theme

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    // Default theme color (Cyberpunk Neon Pink/Purple vibe)
    private val defaultColor = Color(0xFFD039B6)
    // Default background (Cyberpunk/Sci-Fi)
    private val defaultBg = "https://images.unsplash.com/photo-1605806616949-1e87b487cb2a?q=80&w=1080"

    private val _themeColor = MutableStateFlow<Color>(defaultColor)
    val themeColor: StateFlow<Color> = _themeColor.asStateFlow()

    private val _backgroundImageUri = MutableStateFlow<Uri?>(Uri.parse(defaultBg))
    val backgroundImageUri: StateFlow<Uri?> = _backgroundImageUri.asStateFlow()

    fun setThemeColor(color: Color, context: Context) {
        _themeColor.value = color
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("theme_color", color.value.toLong().toInt()).apply()
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
INNER_EOF
