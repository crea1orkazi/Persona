import re

with open("app/src/main/java/com/example/ui/screens/ChatViewModel.kt", "r") as f:
    content = f.read()

# Replace the group chat prompt
pattern_prompt = r'if \(currentChatId < 0\) \{\s*appendLine\("Act as the characters in the group\. Continue the story naturally based on the latest message\."\)\s*appendLine\("You can have one or multiple characters respond\."\)\s*appendLine\("IMPORTANT: Always prefix each character\'s speech with their name and a colon, like \'Name: response\'"\)\s*\}'

replacement_prompt = """if (currentChatId < 0) {
                    appendLine("You are the Narrator of a group chat roleplay featuring multiple characters.")
                    appendLine("Act as an intelligent storyteller. Describe the scene, control the flow of time, and determine which characters speak based on common sense and the natural progression of the story.")
                    appendLine("Do NOT have every character speak all at once. Choose only the most relevant characters to react or speak based on the user's latest message.")
                    appendLine("Format your response naturally as a story paragraph. If a character speaks, you can use bolded names (e.g., **CharacterName:** \\\"Hello!\\\"). Do NOT split your response into multiple separate messages.")
                }"""

content = re.sub(pattern_prompt, replacement_prompt, content)

# Replace the response handling
pattern_response = r'if \(responseText != null\) \{\s*// Extract name prefix if present\s*val lines = responseText\.split\("\\n"\)\.filter \{ it\.isNotBlank\(\) \}\s*for \(line in lines\) \{\s*var role = "AI"\s*if \(currentChatId > 0 && charactersToPrompt\.isNotEmpty\(\)\) \{\s*role = charactersToPrompt\.first\(\)\.name\s*\}\s*var content = line\s*if \(line\.contains\(":"\)\) \{\s*val parts = line\.split\(":", limit = 2\)\s*role = parts\[0\]\.trim\(\)\s*content = parts\[1\]\.trim\(\)\s*\}\s*repository\.insertMessage\(Message\(chatId = currentChatId, role = role, content = content\)\)\s*\}\s*\}'

replacement_response = """if (responseText != null) {
                var role = "Narrator"
                if (currentChatId > 0 && charactersToPrompt.isNotEmpty()) {
                    role = charactersToPrompt.first().name
                }
                var content = responseText.trim()
                
                // If a 1-on-1 character accidentally prefixes their own name, strip it to prevent duplication
                if (currentChatId > 0 && content.startsWith("$role:")) {
                    content = content.removePrefix("$role:").trim()
                }
                repository.insertMessage(Message(chatId = currentChatId, role = role, content = content))
            }"""

content = re.sub(pattern_response, replacement_response, content)

with open("app/src/main/java/com/example/ui/screens/ChatViewModel.kt", "w") as f:
    f.write(content)
