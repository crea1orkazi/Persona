cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/ChatViewModel.kt.patch
--- app/src/main/java/com/example/ui/screens/ChatViewModel.kt
+++ app/src/main/java/com/example/ui/screens/ChatViewModel.kt
@@ -295,9 +295,9 @@
                 appendLine("\n=== STORY MODE ACTIVE | STRICT CHAT LOCK ENABLED ===")
                 if (currentChatId < 0) {
-                    appendLine("Act as the characters in the group. Continue the story naturally based on the latest message.")
-                    appendLine("You can have one or multiple characters respond.")
-                    appendLine("IMPORTANT: Always prefix each character's speech with their name and a colon, like 'Name: response'")
+                    appendLine("You are the Narrator of a group chat roleplay featuring multiple characters.")
+                    appendLine("Act as an intelligent storyteller. Describe the scene, control the flow of time, and determine which characters speak based on common sense and the natural progression of the story.")
+                    appendLine("Do NOT have every character speak all at once. Choose only the most relevant characters to react or speak based on the user's latest message.")
+                    appendLine("Format your response naturally as a story paragraph. If a character speaks, use dialogue format (e.g., **CharacterName:** \"Hello!\").")
                 } else {
                     val charName = charactersToPrompt.first().name
@@ -341,16 +341,13 @@
             
             if (responseText != null) {
-                // Extract name prefix if present
-                val lines = responseText.split("\n").filter { it.isNotBlank() }
-                for (line in lines) {
-                    var role = "AI"
-                    if (currentChatId > 0 && charactersToPrompt.isNotEmpty()) {
-                        role = charactersToPrompt.first().name
-                    }
-                    var content = line
-                    if (line.contains(":")) {
-                        val parts = line.split(":", limit = 2)
-                        role = parts[0].trim()
-                        content = parts[1].trim()
-                    }
-                    repository.insertMessage(Message(chatId = currentChatId, role = role, content = content))
-                }
+                var role = "Narrator"
+                if (currentChatId > 0 && charactersToPrompt.isNotEmpty()) {
+                    role = charactersToPrompt.first().name
+                }
+                var content = responseText.trim()
+                
+                // If a 1-on-1 character accidentally prefixes their own name, strip it to prevent duplication
+                if (currentChatId > 0 && content.startsWith("$role:")) {
+                    content = content.removePrefix("$role:").trim()
+                }
+                repository.insertMessage(Message(chatId = currentChatId, role = role, content = content))
             }
         } catch (e: Exception) {
INNER_EOF
patch -p0 < app/src/main/java/com/example/ui/screens/ChatViewModel.kt.patch
