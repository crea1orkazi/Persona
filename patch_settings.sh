cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/SettingsScreen.kt.patch
--- app/src/main/java/com/example/ui/screens/SettingsScreen.kt
+++ app/src/main/java/com/example/ui/screens/SettingsScreen.kt
@@ -188,14 +188,6 @@
 
             // Custom Background Card
-            val presetWallpapers = listOf(
-                "https://images.unsplash.com/photo-1605806616949-1e87b487cb2a?q=80&w=1080" to "Cyberpunk",
-                "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=1080" to "Fantasy",
-                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1080" to "Dark",
-                "https://images.unsplash.com/photo-1555680202-c86f0e12f086?q=80&w=1080" to "Lo-Fi",
-                "https://images.unsplash.com/photo-1555617781-b5109b68eb51?q=80&w=1080" to "Neon",
-                "https://images.unsplash.com/photo-1506744031584-78330761352a?q=80&w=1080" to "Nature"
-            )
-
             Card(
                 modifier = Modifier.fillMaxWidth(),
                 shape = MaterialTheme.shapes.medium,
@@ -206,37 +198,10 @@
                         Icon(Icons.Filled.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                         Spacer(modifier = Modifier.width(16.dp))
-                        Text("Themes & Backgrounds", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
-                    }
-                    Spacer(modifier = Modifier.height(16.dp))
-                    
-                    Text("Preset Themes", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
-                    Spacer(modifier = Modifier.height(8.dp))
-                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
-                        items(presetWallpapers) { (url, name) ->
-                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
-                                Box(
-                                    modifier = Modifier
-                                        .size(72.dp, 100.dp)
-                                        .clip(RoundedCornerShape(8.dp))
-                                        .border(
-                                            width = if (currentBgUri?.toString() == url) 3.dp else 0.dp,
-                                            color = if (currentBgUri?.toString() == url) MaterialTheme.colorScheme.primary else Color.Transparent,
-                                            shape = RoundedCornerShape(8.dp)
-                                        )
-                                        .clickable { ThemeManager.setBackgroundImage(Uri.parse(url), context) }
-                                ) {
-                                    AsyncImage(
-                                        model = url,
-                                        contentDescription = name,
-                                        contentScale = ContentScale.Crop,
-                                        modifier = Modifier.fillMaxSize()
-                                    )
-                                }
-                                Spacer(modifier = Modifier.height(4.dp))
-                                Text(name, style = MaterialTheme.typography.labelSmall)
-                            }
-                        }
-                    }
-                    
-                    Spacer(modifier = Modifier.height(16.dp))
-                    Text("Custom Theme", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
+                        Text("Custom Background", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
+                    }
+                    Spacer(modifier = Modifier.height(8.dp))
+                    Text("Select a photo to blend the app theme.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
+                    Spacer(modifier = Modifier.height(16.dp))
 
                     Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
@@ -247,5 +212,5 @@
                         ) {
-                            Text("Select From Gallery")
+                            Text("Select Photo")
                         }
                         
INNER_EOF
patch -p0 < app/src/main/java/com/example/ui/screens/SettingsScreen.kt.patch
