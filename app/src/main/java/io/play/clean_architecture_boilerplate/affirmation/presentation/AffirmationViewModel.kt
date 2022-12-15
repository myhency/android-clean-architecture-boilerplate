package io.play.clean_architecture_boilerplate.affirmation.presentation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.play.clean_architecture_boilerplate.R
import io.play.clean_architecture_boilerplate.core.data.local.AffirmationDao
import io.play.clean_architecture_boilerplate.core.data.local.entities.AffirmationEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffirmationViewModel @Inject constructor(
    private val affirmationDao: AffirmationDao
) : ViewModel() {
    var affirmationList by mutableStateOf<List<AffirmationEntity>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            affirmationList = affirmationDao.getAll()
//            affirmationDao.insert(
//                AffirmationEntity(
//                    "1", "I am strong.", "https://darebee.com/images/fitness/muscles-stronger.jpg"
//                )
//            )
        }
    }
}

data class Affirmation(
    val statement: String,
    val id: String,
    @DrawableRes val imageResourceId: Int
)

fun affirmationList(): List<Affirmation> {
    return listOf(
        Affirmation("be good", "1", R.drawable.image1),
        Affirmation("be good", "2", R.drawable.image1),
        Affirmation("be good", "3", R.drawable.image1),
        Affirmation("be good", "4", R.drawable.image1),
        Affirmation("be good", "5", R.drawable.image1),
    )
}