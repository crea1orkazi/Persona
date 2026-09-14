sed -i 's/isLatest = index == messages.size - 1,/isLatest = index == 0,/' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
sed -i '/if (isTyping) {/,+8d' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
