cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/GroupChatScreen.kt.patch
--- app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
+++ app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
@@ -100,10 +100,8 @@
 
     LaunchedEffect(messages.size, isTyping) {
         if (messages.isNotEmpty() || isTyping) {
-            val target = messages.size + if (isTyping) 2 else 1
-            listState.animateScrollToItem(target)
+            listState.animateScrollToItem(0)
         }
     }
 
     if (boundPersonaId == null) {
@@ -352,10 +350,20 @@
                         .fillMaxWidth()
                         .padding(horizontal = 16.dp),
-                    verticalArrangement = Arrangement.spacedBy(8.dp)
+                    verticalArrangement = Arrangement.spacedBy(8.dp),
+                    reverseLayout = true
                 ) {
                     item { Spacer(modifier = Modifier.height(8.dp)) }
-                    itemsIndexed(messages, key = { _, msg -> msg.id }) { index, message ->
+                    
+                    if (isTyping) {
+                        item(key = "typing") {
+                            TypingIndicatorBubble(
+                                role = if (isGroupChat) "AI" else character?.name ?: "AI",
+                                avatarUri = if (isGroupChat) null else character?.avatarUri,
+                                modifier = Modifier.animateItem()
+                            )
+                        }
+                    }
+                    
+                    val reversedMessages = messages.reversed()
+                    itemsIndexed(reversedMessages, key = { _, msg -> msg.id }) { index, message ->
                         val senderCharacter = if (isGroupChat) {
                             allCharacters.find { it.name.equals(message.role, ignoreCase = true) }
@@ -377,10 +385,10 @@
                             isPinned = message.isPinned,
                             avatarUri = senderCharacter?.avatarUri,
-                            isLatest = index == messages.size - 1,
+                            isLatest = index == 0,
                             onRegenerate = { viewModel.regenerateLastMessage() },
                             onEdit = { newText -> viewModel.editMessage(message.id, newText) },
                             onDelete = { viewModel.deleteMessage(message.id) },
                             onTogglePin = { viewModel.toggleMessagePin(message.id, message.isPinned) },
                             onReaction = { reaction -> viewModel.setReaction(message.id, reaction) },
                             modifier = Modifier.animateItem()
                         )
                     }
-                    if (isTyping) {
-                        item(key = "typing") {
-                            TypingIndicatorBubble(
-                                role = if (isGroupChat) "AI" else character?.name ?: "AI",
-                                avatarUri = if (isGroupChat) null else character?.avatarUri,
-                                modifier = Modifier.animateItem()
-                            )
-                        }
-                    }
                     item { Spacer(modifier = Modifier.height(8.dp)) }
                 }
INNER_EOF
patch -p0 < app/src/main/java/com/example/ui/screens/GroupChatScreen.kt.patch
