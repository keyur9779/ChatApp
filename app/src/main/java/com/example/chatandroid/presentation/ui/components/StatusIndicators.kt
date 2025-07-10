package com.example.chatandroid.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Composable for displaying a network status banner.
 * 
 * @param isOnline Whether the device is online
 * @param isVisible Whether the banner should be visible
 * @param onDismiss Callback for when the banner is dismissed
 */
@Composable
fun NetworkStatusBanner(
    isOnline: Boolean,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible && !isOnline,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Network Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You're offline. Messages will be sent when you reconnect.",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                IconButton(onClick = onDismiss) {
                    Text(
                        text = "×",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

/**
 * Composable for displaying a connection status indicator.
 *
 * @param isConnected Whether the app is connected to the chat service
 * @param error Connection error message
 * @param isErrorVisible Whether the error message should be visible
 * @param onDismissError Callback for when the error is dismissed
 */
@Composable
fun ConnectionStatusIndicator(
    isConnected: Boolean,
    error: String = "",
    isErrorVisible: Boolean = false,
    onDismissError: () -> Unit = {}
) {
    Column {
        // Connection status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isConnected) Color.Green else Color.Red,
                        shape = MaterialTheme.shapes.small
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isConnected) "Connected" else "Not Connected",
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Error message
        AnimatedVisibility(
            visible = isErrorVisible && error.isNotEmpty()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(
                    onClick = onDismissError,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(
                        text = "×",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Composable for displaying an empty state when there are no messages.
 */
@Composable
fun EmptyStateView(
    isConnected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "No Messages",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isConnected) 
                    "No messages yet" 
                else 
                    "Connect to start chatting",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Composable for displaying a typing indicator.
 *
 * @param isTyping Whether someone is typing
 * @param userName Name of the user who is typing
 */
@Composable
fun TypingIndicator(
    isTyping: Boolean,
    userName: String
) {
    AnimatedVisibility(visible = isTyping) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$userName is typing...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
