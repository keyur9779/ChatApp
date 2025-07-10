package com.example.chatandroid.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Composable for displaying a dialog to create a new chat.
 *
 * @param isVisible Whether the dialog is visible
 * @param onDismiss Callback for when the dialog is dismissed
 * @param onCreateChat Callback for when a new chat is created
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCreateChat: (String) -> Unit
) {
    var chatName by remember { mutableStateOf("") }
    
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Create New Chat",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = chatName,
                        onValueChange = { chatName = it },
                        label = { Text("Chat Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancel")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (chatName.isNotBlank()) {
                                    onCreateChat(chatName)
                                    chatName = ""
                                    onDismiss()
                                }
                            },
                            enabled = chatName.isNotBlank()
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}
