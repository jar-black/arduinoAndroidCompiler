package com.arduinocompiler.ui.screens.compile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompileScreen(
    projectId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToUpload: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Compile - Project $projectId")
                Button(onClick = onNavigateToUpload) {
                    Text("Upload")
                }
            }
        }
    }
}
