package com.example.tvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tvapp.ui.list.ShowListScreen
import com.example.tvapp.ui.theme.TVAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TVAppTheme {
                ShowListScreen(
                    onShowClick = { showId ->
                        // Navigation to the Detail screen
                    }
                )
            }
        }
    }
}