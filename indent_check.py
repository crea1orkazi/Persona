with open("app/src/main/java/com/example/ui/screens/GroupChatScreen.kt", "r") as f:
    lines = f.readlines()

indent = 0
for i, line in enumerate(lines):
    # This is a naive check. 
    stripped = line.strip()
    
    # Decrease indent for lines starting with }
    if stripped.startswith('}'):
        indent -= 1
        
    print(f"{i+1:4} | {indent:2} | {'  ' * indent}{stripped}")
    
    # Calculate for next line
    for char in stripped:
        if char == '{': indent += 1
        elif char == '}': indent -= 1
        
    if stripped.startswith('}'):
        indent += 1 # we already decreased it for this line
