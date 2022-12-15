package io.play.clean_architecture_boilerplate.affirmation.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.lazy.rememberLazyListState
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
    var isNewAffirmationDialogShown by mutableStateOf(false)
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

    fun onNewAffirmationClick() {
        isNewAffirmationDialogShown = true
    }

    fun onNewAffirmationClose() {
        isNewAffirmationDialogShown = false
    }

    fun onDismissButtonClicked(
        affirmationStatement: String,
        todayFeeling: String,
        date: String,
        imageUrl: String
    ) {
        onNewAffirmationClose()
        viewModelScope.launch {
            affirmationDao.insert(
                AffirmationEntity(
                     affirmationStatement, todayFeeling, date, imageUrl
                )
            )
            affirmationList = affirmationDao.getAll()
        }
    }
}