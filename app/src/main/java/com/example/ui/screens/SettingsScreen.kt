package com.example.ui.screens

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette as AndroidPalette
import com.example.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDynamicTheme by ThemeManager.isDynamicTheme.collectAsState()
    val currentThemeColor by ThemeManager.themeColor.collectAsState()
    val currentBgUri by ThemeManager.backgroundImageUri.collectAsState()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // Ignore if permission taking fails
                }
                
                ThemeManager.setBackgroundImage(uri, context)
                
                // Extract color using Palette
                withContext(Dispatchers.IO) {
                    try {
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, uri)
                            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                decoder.isMutableRequired = true
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }
                        
                        val palette = AndroidPalette.from(bitmap).generate()
                        val vibrant = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb
                        if (vibrant != null) {
                            withContext(Dispatchers.Main) {
                                ThemeManager.setThemeColor(Color(vibrant), context)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme & Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Theme Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("App Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Theme (Material You) Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDynamicTheme) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent)
                            .border(
                                width = if (isDynamicTheme) 2.dp else 1.dp,
                                color = if (isDynamicTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                ThemeManager.setDynamicTheme(!isDynamicTheme, context)
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Dynamic Theme (Material You)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Adapts colors dynamically from your device wallpaper and system palette",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = isDynamicTheme,
                            onCheckedChange = { ThemeManager.setDynamicTheme(it, context) }
                        )
                    }

                    // Live Palette Preview when Dynamic Theme is active
                    if (isDynamicTheme) {
                        Spacer(modifier = Modifier.height(14.dp))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val dynamicScheme = dynamicDarkColorScheme(context)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "Active System Wallpaper Palette",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf(
                                        "Primary" to dynamicScheme.primary,
                                        "Secondary" to dynamicScheme.secondary,
                                        "Tertiary" to dynamicScheme.tertiary,
                                        "Surface" to dynamicScheme.surfaceVariant
                                    ).forEach { (label, color) ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                "Dynamic color enabled. (System wallpaper extraction requires Android 12+)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Other Modes", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Monochrome Option
                    val isMonochromeSelected = !isDynamicTheme && currentThemeColor == Color.White
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isMonochromeSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent)
                            .border(
                                width = if (isMonochromeSelected) 2.dp else 1.dp,
                                color = if (isMonochromeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                ThemeManager.setDynamicTheme(false, context)
                                ThemeManager.setThemeColor(Color.White, context)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Monochrome / Minimalist", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("High-contrast black & white dark mode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isMonochromeSelected) {
                            Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Extracted Gallery Wallpaper Option
                    if (currentBgUri != null && currentThemeColor != Color.White) {
                        val isPhotoColorSelected = !isDynamicTheme && currentThemeColor != Color.White
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isPhotoColorSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent)
                            .border(
                                width = if (isPhotoColorSelected) 2.dp else 1.dp,
                                color = if (isPhotoColorSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                ThemeManager.setDynamicTheme(false, context)
                            }
                            .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(currentThemeColor)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Gallery Photo Palette", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Extracted from your custom background image", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (isPhotoColorSelected) {
                                Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Custom Background Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Custom Background", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select a photo from your gallery to blend with the app theme.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Select From Gallery")
                        }
                        
                        if (currentBgUri != null) {
                            IconButton(
                                onClick = { 
                                    ThemeManager.setBackgroundImage(null, context)
                                    ThemeManager.setDynamicTheme(true, context)
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove Background", tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}
