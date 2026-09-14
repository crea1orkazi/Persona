import re

with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "r") as f:
    content = f.read()

pattern = r'fun parseRoleplayText\(text: String, isUser: Boolean\): AnnotatedString \{.*?return buildAnnotatedString \{.*?\}\s*\}'

replacement = """fun parseRoleplayText(text: String, isUser: Boolean): AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString { append(text) }
}"""

new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "w") as f:
    f.write(new_content)
