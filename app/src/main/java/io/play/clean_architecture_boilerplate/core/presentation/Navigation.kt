package io.play.clean_architecture_boilerplate.core.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.play.clean_architecture_boilerplate.affirmation.presentation.AffirmationDetailScreen
import io.play.clean_architecture_boilerplate.affirmation.presentation.AffirmationScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ScreenRoutes.AffirmationScreen.route) {
        composable(ScreenRoutes.AffirmationScreen.route) {
            AffirmationScreen(navController, onAffirmationCardClicked = {
                navController.navigate(ScreenRoutes.AffirmationDetailScreen.route)
            })
        }
        composable(ScreenRoutes.AffirmationDetailScreen.route) {
            AffirmationDetailScreen()
        }
    }
}

sealed class ScreenRoutes(val route:String) {
    object AffirmationScreen:ScreenRoutes("affirmation_screen")
    object AffirmationDetailScreen:ScreenRoutes("affirmation_detail_screen")
}