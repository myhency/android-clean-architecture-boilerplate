package io.play.clean_architecture_boilerplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import io.play.clean_architecture_boilerplate.core.presentation.Navigation
import io.play.clean_architecture_boilerplate.ui.theme.CleanarchitectureboilerplateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanarchitectureboilerplateTheme {
                Navigation()
            }
        }
    }
}

