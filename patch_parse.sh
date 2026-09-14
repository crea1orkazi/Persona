cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/GroupChatScreen.kt.patch
--- app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
+++ app/src/main/java/com/example/ui/screens/GroupChatScreen.kt
@@ -533,14 +533,35 @@
 
     return buildAnnotatedString {
-        val parts = text.split("*")
-        for (i in parts.indices) {
-            if (i % 2 == 0) {
-                append(parts[i])
-            } else {
-                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = actionColor)) {
-                    append("*${parts[i]}*") // Keep the asterisks for style, but italicize and fade
-                }
-            }
-        }
+        // First, handle bold (**text**)
+        // We will do this by keeping track of styles manually or using a simple regex-like approach.
+        // Since we are building an AnnotatedString, we can use a more robust parsing mechanism.
+        
+        var i = 0
+        while (i < text.length) {
+            if (i + 1 < text.length && text[i] == '*' && text[i + 1] == '*') {
+                // Find closing **
+                val end = text.indexOf("**", i + 2)
+                if (end != -1) {
+                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
+                        append(text.substring(i + 2, end))
+                    }
+                    i = end + 2
+                    continue
+                }
+            }
+            if (text[i] == '*') {
+                // Find closing *
+                val end = text.indexOf('*', i + 1)
+                if (end != -1 && (end == text.length - 1 || text[end + 1] != '*')) {
+                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = actionColor)) {
+                        append(text.substring(i, end + 1))
+                    }
+                    i = end + 1
+                    continue
+                }
+            }
+            append(text[i].toString())
+            i++
+        }
     }
 }
INNER_EOF
patch -p0 < app/src/main/java/com/example/ui/screens/GroupChatScreen.kt.patch
