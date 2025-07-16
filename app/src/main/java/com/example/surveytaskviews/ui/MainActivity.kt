package com.example.surveytaskviews.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.surveytaskviews.ui.form.FormScreen
import com.example.surveytaskviews.ui.submitted.SubmittedDataScreen
import com.example.surveytaskviews.ui.theme.SurveyTaskViewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyTaskViewsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "form_screen"
                    ) {
                        composable("form_screen") {
                            FormScreen(onNavigateToSubmittedData = {
                                navController.navigate("submitted_data_screen")
                            })
                        }
                        composable("submitted_data_screen") {
                            SubmittedDataScreen()
                        }
                    }
                }
            }
        }
    }
}