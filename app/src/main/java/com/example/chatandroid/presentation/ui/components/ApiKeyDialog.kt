package com.example.chatandroid.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Composable for displaying a dialog to enter the API key.
 *
 * @param isVisible Whether the dialog is visible
 * @param onConnect Callback for when the connect button is clicked
 * @param onDismiss Callback for when the dialog is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyDialog(
    isVisible: Boolean,
    onConnect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var apiKey by remember { mutableStateOf("2ZilnOus6sDs7od7bbVYQgx8LlgAfabf7yGLJlUt") }
    
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
                        text = "Connect to Chat",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
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
                                if (apiKey.isNotBlank()) {
                                    onConnect(apiKey)
                                }
                            },
                            enabled = apiKey.isNotBlank()
                        ) {
                            Text("Connect")
                        }
                    }
                }
            }
        }
    }
}
