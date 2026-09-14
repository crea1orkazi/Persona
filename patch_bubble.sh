sed -i 's/import androidx.compose.foundation.background/import androidx.compose.foundation.background\nimport androidx.compose.animation.*\nimport androidx.compose.animation.core.*/' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
sed -i '647,651c\
    var visible by remember { mutableStateOf(false) }\
    LaunchedEffect(Unit) { visible = true }\
\
    AnimatedVisibility(\
        visible = visible,\
        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),\
        modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)\
    ) {\
        Box(\
            contentAlignment = alignment\
        ) {\
            Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {' app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
