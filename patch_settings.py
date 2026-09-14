import re

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

# I will just replace the broken section
pattern = r'// Custom Background Card.*?Text\("Custom Background", style = MaterialTheme\.typography\.titleMedium, fontWeight = FontWeight\.SemiBold\)'

replacement = """// Custom Background Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Custom Background", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)"""

new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(new_content)
