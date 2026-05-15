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
import com.takuchan.suudoku.logic.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    language: Language,
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
                text = getWelcomeMessage(language),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getChallengeMessage(language),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            DifficultyButton(
                text = getDifficultyName(Difficulty.BEGINNER, language),
                onClick = { onStartGame(Difficulty.BEGINNER) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            DifficultyButton(
                text = getDifficultyName(Difficulty.INTERMEDIATE, language),
                onClick = { onStartGame(Difficulty.INTERMEDIATE) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            DifficultyButton(
                text = getDifficultyName(Difficulty.ADVANCED, language),
                onClick = { onStartGame(Difficulty.ADVANCED) },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

fun getWelcomeMessage(language: Language) = when (language) {
    Language.ENGLISH -> "Welcome back!"
    Language.JAPANESE -> "おかえりなさい！"
    Language.CHINESE -> "欢迎回来！"
    Language.KOREAN -> "어서 오세요!"
}

fun getChallengeMessage(language: Language) = when (language) {
    Language.ENGLISH -> "Select your challenge level"
    Language.JAPANESE -> "難易度を選択してください"
    Language.CHINESE -> "选择您的挑战级别"
    Language.KOREAN -> "난이도를 선택하세요"
}

fun getDifficultyName(difficulty: Difficulty, language: Language) = when (difficulty) {
    Difficulty.BEGINNER -> when (language) {
        Language.ENGLISH -> "Beginner"
        Language.JAPANESE -> "初級"
        Language.CHINESE -> "初学者"
        Language.KOREAN -> "초보자"
    }
    Difficulty.INTERMEDIATE -> when (language) {
        Language.ENGLISH -> "Intermediate"
        Language.JAPANESE -> "中級"
        Language.CHINESE -> "中级"
        Language.KOREAN -> "중급자"
    }
    Difficulty.ADVANCED -> when (language) {
        Language.ENGLISH -> "Advanced"
        Language.JAPANESE -> "上級"
        Language.CHINESE -> "高级"
        Language.KOREAN -> "상급자"
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
