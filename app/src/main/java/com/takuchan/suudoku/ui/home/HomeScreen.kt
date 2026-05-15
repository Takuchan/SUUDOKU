package com.takuchan.suudoku.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takuchan.suudoku.logic.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartGame: (Difficulty) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SUUDOKU", fontWeight = FontWeight.Black, fontSize = 28.sp) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select your challenge level",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            DifficultyButton(
                text = "Beginner",
                onClick = { onStartGame(Difficulty.BEGINNER) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            DifficultyButton(
                text = "Intermediate",
                onClick = { onStartGame(Difficulty.INTERMEDIATE) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            DifficultyButton(
                text = "Advanced",
                onClick = { onStartGame(Difficulty.ADVANCED) },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun DifficultyButton(
    text: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = text, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
    }
}
