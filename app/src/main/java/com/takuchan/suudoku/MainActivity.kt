package com.takuchan.suudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.takuchan.suudoku.ui.game.GameScreen
import com.takuchan.suudoku.ui.game.GameViewModel
import com.takuchan.suudoku.ui.home.HomeScreen
import com.takuchan.suudoku.ui.settings.SettingsScreen
import com.takuchan.suudoku.ui.settings.SettingsViewModel
import com.takuchan.suudoku.ui.theme.SUUDOKUTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SUUDOKUTheme {
                val navController = rememberNavController()
                val gameViewModel: GameViewModel = viewModel()
                val settingsViewModel: SettingsViewModel = viewModel()
                val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onStartGame = { difficulty ->
                                gameViewModel.startGame(
                                    difficulty = difficulty,
                                    maxMistakes = settingsState.maxMistakes,
                                    isTimerEnabled = settingsState.isTimerEnabled
                                )
                                navController.navigate("game")
                            },
                            onNavigateToSettings = {
                                navController.navigate("settings")
                            }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("game") {
                        GameScreen(
                            viewModel = gameViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
