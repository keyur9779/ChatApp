package com.example.chatandroid.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A status bar that displays network connectivity status or other errors.
 *
 * @param isOnline Whether the device is online
 * @param isConnected Whether the WebSocket is connected
 * @param showNetworkError Whether to show a network error
 * @param showConnectionError Whether to show a connection error
 * @param errorMessage Error message to display
 */
@Composable
fun NetworkStatusBar(
    isOnline: Boolean,
    isConnected: Boolean,
    showNetworkError: Boolean = false,
    showConnectionError: Boolean = false,
    errorMessage: String = ""
) {
    AnimatedVisibility(
        visible = !isOnline || showNetworkError || showConnectionError || (isOnline && !isConnected),
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        val backgroundColor = when {
            !isOnline -> Color.Gray
            showNetworkError -> Color.Red
            showConnectionError -> Color.Red
            !isConnected -> Color(0xFFFF9800) // Orange for connecting state
            else -> Color.Transparent
        }
        
        val icon = when {
            !isOnline -> Icons.Filled.Warning
            showNetworkError || showConnectionError -> Icons.Filled.Warning
            !isConnected -> Icons.Filled.Warning
            else -> null
        }
        
        val message = when {
            !isOnline -> "Offline Mode - Messages will be queued"
            showNetworkError || showConnectionError -> errorMessage.ifEmpty { "Connection error" }
            !isConnected -> "Connecting..."
            else -> ""
        }
        
        if (message.isNotEmpty()) {
            StatusMessage(
                backgroundColor = backgroundColor,
                icon = icon,
                message = message
            )
        }
    }
}

@Composable
private fun StatusMessage(
    backgroundColor: Color,
    icon: ImageVector?,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = message,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
