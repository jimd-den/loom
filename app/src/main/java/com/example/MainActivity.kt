package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.LoomRepositoryImpl
import com.example.presentation.ui.*
import com.example.presentation.viewmodel.LoomViewModel
import com.example.presentation.viewmodel.LoomViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Clean Architecture dependency orchestration
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = LoomRepositoryImpl(db)
        val factory = LoomViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[LoomViewModel::class.java]

        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val activeKey by viewModel.apiKey.collectAsState()

                // Route automatically: if API key is not configured, show welcome/onboarding key screen first
                val startRoute = if (activeKey.isEmpty()) "welcome" else "home"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startRoute
                    ) {
                        composable("welcome") {
                            WelcomeScreen(
                                viewModel = viewModel,
                                onNavigateNext = {
                                    navController.navigate("home") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToInterview = { navController.navigate("interview") },
                                onNavigateToCanvas = { navController.navigate("canvas") },
                                onNavigateToRecallQueue = { navController.navigate("recall_queue") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }

                        composable("interview") {
                            GoalInterviewScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToCanvas = { 
                                    navController.navigate("canvas") {
                                        popUpTo("interview") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("canvas") {
                            IdeaCanvasScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigate("home") },
                                onNavigateToDiscovery = { navController.navigate("discovery") }
                            )
                        }

                        composable("discovery") {
                            SourceLaneScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToReader = { navController.navigate("reader") }
                            )
                        }

                        composable("reader") {
                            ReadingViewScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToRecallQueue = { navController.navigate("recall_queue") }
                            )
                        }

                        composable("recall_queue") {
                            RecallQueueScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
