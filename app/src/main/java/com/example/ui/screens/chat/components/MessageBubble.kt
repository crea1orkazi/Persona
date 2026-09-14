package com.example.ui.screens.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun parseRoleplayText(text: String, isUser: Boolean): AnnotatedString {
    val actionColor = if (isUser) 
        Color.White.copy(alpha = 0.7f) 
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
}

@Composable
fun TypingIndicatorBubble(role: String, avatarUri: String? = null, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        val avatarColor = Color(
            android.graphics.Color.HSVToColor(floatArrayOf((role.hashCode() % 360).toFloat(), 0.6f, 0.9f))
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(avatarColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri != null) {
                AsyncImage(
                    model = android.net.Uri.parse(avatarUri),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = role.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = role,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                var dotCount by remember { mutableIntStateOf(0) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(400)
                        dotCount = (dotCount + 1) % 4
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant
                    Box(modifier = Modifier.size(6.dp).background(if (dotCount > 0) dotColor else dotColor.copy(alpha = 0.3f), CircleShape))
                    Box(modifier = Modifier.size(6.dp).background(if (dotCount > 1) dotColor else dotColor.copy(alpha = 0.3f), CircleShape))
                    Box(modifier = Modifier.size(6.dp).background(if (dotCount > 2) dotColor else dotColor.copy(alpha = 0.3f), CircleShape))
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    messageId: Int,
    role: String,
    content: String,
    isUser: Boolean,
    timestamp: Long,
    isPrevSameSender: Boolean = false,
    isNextSameSender: Boolean = false,
    reaction: String? = null,
    isPinned: Boolean = false,
    avatarUri: String? = null,
    isLatest: Boolean = false,
    onRegenerate: (() -> Unit)? = null,
    onEdit: ((String) -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onTogglePin: (() -> Unit)? = null,
    onReaction: ((String?) -> Unit)? = null,
    onTextAnimated: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    
    val shape = remember(isUser, isPrevSameSender, isNextSameSender) {
        if (isUser) {
            when {
                !isPrevSameSender && !isNextSameSender -> RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)
                !isPrevSameSender && isNextSameSender -> RoundedCornerShape(20.dp, 20.dp, 20.dp, 6.dp)
                isPrevSameSender && isNextSameSender -> RoundedCornerShape(20.dp, 6.dp, 20.dp, 6.dp)
                else -> RoundedCornerShape(20.dp, 6.dp, 20.dp, 4.dp)
            }
        } else {
            when {
                !isPrevSameSender && !isNextSameSender -> RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)
                !isPrevSameSender && isNextSameSender -> RoundedCornerShape(20.dp, 20.dp, 6.dp, 20.dp)
                isPrevSameSender && isNextSameSender -> RoundedCornerShape(6.dp, 20.dp, 6.dp, 20.dp)
                else -> RoundedCornerShape(6.dp, 20.dp, 4.dp, 20.dp)
            }
        }
    }

    val timeString = remember(timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    }

    var animatedText by rememberSaveable(messageId) {
        mutableStateOf(if (isLatest && !isUser) "" else content)
    }
    
    var isEditing by remember { mutableStateOf(false) }
    var editValue by remember(content) { mutableStateOf(content) }

    LaunchedEffect(content, isLatest) {
        if (isLatest && !isUser && animatedText.length < content.length) {
            val startIdx = animatedText.length
            for (i in startIdx..content.length) {
                animatedText = content.substring(0, i)
                onTextAnimated?.invoke()
                delay(15) // 15ms per letter for smooth typing
            }
            animatedText = content
        } else {
            animatedText = content
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val bottomSpacing = if (isNextSameSender) 3.dp else 10.dp

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
        modifier = modifier.fillMaxWidth().padding(bottom = bottomSpacing)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = alignment
        ) {
            Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser && role != "System") {
                        if (!isPrevSameSender) {
                            val avatarColor = Color(
                                android.graphics.Color.HSVToColor(floatArrayOf((role.hashCode() % 360).toFloat(), 0.6f, 0.9f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(avatarColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (avatarUri != null) {
                                    AsyncImage(
                                        model = android.net.Uri.parse(avatarUri),
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = role.take(1).uppercase(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            // Keep continuous messages from same sender aligned under the avatar
                            Spacer(modifier = Modifier.width(44.dp))
                        }
                    }
                    
                    Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                        if (!isUser && role != "System" && !isPrevSameSender) {
                            Text(
                                text = role,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                                .background(bgColor, shape)
                                .padding(16.dp)
                                .widthIn(max = 320.dp)
                        ) {
                            if (isEditing) {
                                Column {
                                    OutlinedTextField(
                                        value = editValue,
                                        onValueChange = { editValue = it },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                    ) {
                                        TextButton(onClick = { isEditing = false }) {
                                            Text("Cancel", color = textColor)
                                        }
                                        TextButton(onClick = {
                                            isEditing = false
                                            onEdit?.invoke(editValue)
                                        }) {
                                            Text("Save", color = textColor, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = parseRoleplayText(animatedText, isUser),
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                var showReactionPicker by remember { mutableStateOf(false) }

                if (reaction != null) {
                    Box(
                        modifier = Modifier
                            .padding(start = if (isUser) 0.dp else 44.dp, top = 4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { onReaction?.invoke(null) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = reaction, fontSize = 14.sp)
                    }
                }

                if (showReactionPicker) {
                    Row(
                        modifier = Modifier.padding(start = if (isUser) 0.dp else 44.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("❤️", "😂", "😮", "😢", "🔥", "👍").forEach { emoji ->
                            Text(
                                text = emoji,
                                modifier = Modifier
                                    .clickable {
                                        onReaction?.invoke(emoji)
                                        showReactionPicker = false
                                    }
                                    .padding(4.dp),
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                if (role != "System" && animatedText == content && (!isNextSameSender || isEditing)) {
                    Row(
                        modifier = Modifier.padding(start = if (isUser) 0.dp else 44.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        if (!isUser) {
                            IconButton(onClick = { showReactionPicker = !showReactionPicker }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Face, contentDescription = "React", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(content)) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onTogglePin?.invoke() }, modifier = Modifier.size(28.dp)) {
                            Icon(if (isPinned) Icons.Filled.Star else Icons.Filled.StarBorder, contentDescription = "Pin", modifier = Modifier.size(16.dp), tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { isEditing = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDelete?.invoke() }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isLatest && onRegenerate != null && !isUser) {
                            IconButton(onClick = onRegenerate, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Regenerate", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
