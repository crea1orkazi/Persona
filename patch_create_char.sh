sed -i '/Scaffold(/a \        containerColor = androidx.compose.ui.graphics.Color.Transparent,' app/src/main/java/com/example/ui/screens/CreateCharacterScreen.kt
sed -i 's/containerColor = MaterialTheme.colorScheme.background,/containerColor = androidx.compose.ui.graphics.Color.Transparent,/' app/src/main/java/com/example/ui/screens/CreateCharacterScreen.kt
sed -i 's/containerColor = MaterialTheme.colorScheme.surface/containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)/' app/src/main/java/com/example/ui/screens/CreateCharacterScreen.kt
