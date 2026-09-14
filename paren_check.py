with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "r") as f:
    text = f.read()
print("Parens:", text.count('(') - text.count(')'))
print("Braces:", text.count('{') - text.count('}'))
print("Brackets:", text.count('[') - text.count(']'))
