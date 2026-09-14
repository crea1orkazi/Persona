import re

with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "r") as f:
    content = f.read()

pattern = r'fun parseRoleplayText\(text: String, isUser: Boolean\): AnnotatedString \{.*?return buildAnnotatedString \{.*?\}\s*\}'

replacement = """fun parseRoleplayText(text: String, isUser: Boolean): AnnotatedString {
    val actionColor = if (isUser) 
        androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f) 
    else 
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            if (i + 1 < text.length && text[i] == '*' && text[i + 1] == '*') {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                    continue
                }
            }
            if (text[i] == '*') {
                val end = text.indexOf('*', i + 1)
                if (end != -1) {
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = actionColor)) {
                        append(text.substring(i, end + 1))
                    }
                    i = end + 1
                    continue
                }
            }
            append(text[i].toString())
            i++
        }
    }
}"""

content = re.sub(pattern, replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "w") as f:
    f.write(content)
