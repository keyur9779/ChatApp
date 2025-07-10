package com.example.chatandroid.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.chatandroid.data.model.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable for displaying a single chat message.
 * 
 * @param message The message to display
 * @param onLikeMessage Callback for when the like button is clicked
 * @param onRetryMessage Callback for when the retry button is clicked
 */
@Composable
fun MessageItem(
    message: Message,
    onLikeMessage: (String) -> Unit = {},
    onRetryMessage: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (message.isSent) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(4.dp)
                .widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isSent) 
                    MaterialTheme.colorScheme.primaryContainer
                else 
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                // Message content
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isSent) 
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else 
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                // Status indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Format and display timestamp
                    val formattedTime = SimpleDateFormat(
                        "HH:mm, dd MMM",
                        Locale.getDefault()
                    ).format(Date(message.timestamp))
                    
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (message.isSent) 
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else 
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Error indicator
                        if (message.isError) {
                            IconButton(
                                onClick = { onRetryMessage(message.id) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        // Sending indicator
                        if (message.isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        
                        // Like button for received messages
                        if (!message.isSent) {
                            IconButton(
                                onClick = { onLikeMessage(message.id) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 4.dp)
                            ) {
                                Text(
                                    text = if (message.isLiked) "❤️" else "♡",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        // Read indicator for sent messages
                        if (message.isSent && message.isRead) {
                            Text(
                                text = "✓✓",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        } else if (message.isSent) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
