package com.example.tvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tvapp.ui.detail.ShowDetailScreen
import com.example.tvapp.ui.list.ShowListScreen
import com.example.tvapp.ui.theme.TVAppTheme

private const val ROUTE_LIST = "list"
private const val ROUTE_DETAIL = "detail/{showId}"
private const val ARG_SHOW_ID = "showId"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TVAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = ROUTE_LIST) {
                    composable(ROUTE_LIST) {
                        ShowListScreen(
                            onShowClick = { showId ->
                                navController.navigate("detail/$showId")
                            }
                        )
                    }

                    composable(
                        route = ROUTE_DETAIL,
                        arguments = listOf(navArgument(ARG_SHOW_ID) { type = NavType.IntType })
                    ) { backStackEntry ->
                        val showId = backStackEntry.arguments?.getInt(ARG_SHOW_ID) ?: return@composable
                        ShowDetailScreen(showId = showId)
                    }
                }
            }
        }
    }
}