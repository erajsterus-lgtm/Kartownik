package com.erakles.kartownik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erakles.kartownik.ui.screens.AboutScreen
import com.erakles.kartownik.ui.screens.AddCardScreen
import com.erakles.kartownik.ui.screens.CardDetailScreen
import com.erakles.kartownik.ui.screens.HomeScreen
import com.erakles.kartownik.ui.theme.KartownikTheme
import com.erakles.kartownik.ui.viewmodel.CardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KartownikTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onCardClick = { cardId ->
                                navController.navigate("detail/$cardId")
                            },
                            onAddCardClick = {
                                navController.navigate("add")
                            },
                            onAboutClick = {
                                navController.navigate("about")
                            }
                        )
                    }

                    composable("add") {
                        AddCardScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "detail/{cardId}",
                        arguments = listOf(
                            navArgument("cardId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val cardId = backStackEntry.arguments?.getLong("cardId") ?: -1L
                        CardDetailScreen(
                            cardId = cardId,
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onEditClick = { editId ->
                                navController.navigate("edit/$editId")
                            }
                        )
                    }

                    composable(
                        route = "edit/{cardId}",
                        arguments = listOf(
                            navArgument("cardId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val cardId = backStackEntry.arguments?.getLong("cardId") ?: -1L
                        AddCardScreen(
                            viewModel = viewModel,
                            cardIdToEdit = cardId,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("about") {
                        AboutScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
