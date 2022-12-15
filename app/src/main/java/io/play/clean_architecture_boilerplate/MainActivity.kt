package io.play.clean_architecture_boilerplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.play.clean_architecture_boilerplate.affirmation.presentation.AffirmationScreen
import io.play.clean_architecture_boilerplate.affirmation.presentation.AffirmationViewModel
import io.play.clean_architecture_boilerplate.ui.theme.CleanarchitectureboilerplateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val affirmationViewModel: AffirmationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanarchitectureboilerplateTheme {
                AffirmationScreen()
            }
        }
    }
}

