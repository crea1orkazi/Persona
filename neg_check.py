with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "r") as f:
    text = f.read()

count = 0
for i, c in enumerate(text):
    if c == '{': count += 1
    elif c == '}': count -= 1
    if count < 0:
        print(f"Negative at index {i}")
print("Final:", count)
