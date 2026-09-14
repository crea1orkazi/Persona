package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeManager
import com.example.utils.NetworkMonitor

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Load theme from preferences
    ThemeManager.loadFromPrefs(this)
    
    enableEdgeToEdge()
    setContent {
      val networkMonitor = remember { NetworkMonitor(this) }
      val isConnected by networkMonitor.isConnected.collectAsState(initial = true)

      val isDynamicTheme by ThemeManager.isDynamicTheme.collectAsState()
      val themeColor by ThemeManager.themeColor.collectAsState()
      val bgImageUri by ThemeManager.backgroundImageUri.collectAsState()
      
      val appPrefs = remember { getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE) }
      var showStoragePrompt by remember { androidx.compose.runtime.mutableStateOf(!appPrefs.contains("unlimited_memory")) }

      MyApplicationTheme(
          customPrimaryColor = themeColor,
          dynamicColor = isDynamicTheme
      ) {
        if (!isConnected) {
            AlertDialog(
                onDismissRequest = { finish() },
                title = { Text("No Internet Connection") },
                text = { Text("This application requires an active internet connection to function. Please check your network settings.") },
                confirmButton = {
                    TextButton(onClick = { finish() }) {
                        Text("Exit App")
                    }
                },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            )
        }
            
        if (showStoragePrompt) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Device Storage Permission") },
                text = { Text("Allow the app to use your device's storage for unlimited AI memory? If denied, the AI will only remember the last few messages (Standard Mode).") },
                confirmButton = {
                    TextButton(onClick = { 
                        appPrefs.edit().putBoolean("unlimited_memory", true).apply()
                        showStoragePrompt = false
                    }) {
                        Text("Allow")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        appPrefs.edit().putBoolean("unlimited_memory", false).apply()
                        showStoragePrompt = false
                    }) {
                        Text("Deny")
                    }
                },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            )
        }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (bgImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = bgImageUri),
                    contentDescription = "Custom Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // App Content
            AppNavigation()
        }
      }
    }
  }
}
