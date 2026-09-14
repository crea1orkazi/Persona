#!/bin/bash

# Modify LaunchedEffect for scroll
sed -i 's/val target = messages.size + if (isTyping) 2 else 1/listState.animateScrollToItem(0)/' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
sed -i 's/listState.animateScrollToItem(target)//' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt

# Add reverseLayout to LazyColumn
sed -i 's/verticalArrangement = Arrangement.spacedBy(8.dp)/verticalArrangement = Arrangement.spacedBy(8.dp), reverseLayout = true/' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt

