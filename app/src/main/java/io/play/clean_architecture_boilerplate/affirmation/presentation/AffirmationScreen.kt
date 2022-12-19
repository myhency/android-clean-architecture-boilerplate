package io.play.clean_architecture_boilerplate.affirmation.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.play.clean_architecture_boilerplate.core.data.local.entities.AffirmationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AffirmationScreen(
    navController: NavController,
    onAffirmationCardClicked: () -> Unit,
    affirmationViewModel: AffirmationViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { affirmationViewModel.onNewAffirmationClick() },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = scrollState
        ) {
            items(
                affirmationViewModel.affirmationList,
                key = { affirmationListItem -> affirmationListItem.id }
            ) {
                AffirmationCard(it, onAffirmationCardClicked)
            }
        }
    }

    if (affirmationViewModel.isNewAffirmationDialogShown) {
        NewAffirmationDialog(
            onDismissButtonClicked = { affirmationStatement, todayFeeling, date, imageUrl ->
                affirmationViewModel.onDismissButtonClicked(
                    affirmationStatement,
                    todayFeeling,
                    date,
                    imageUrl
                ) {
                    coroutineScope.launch { scrollState.animateScrollToItem(0) }
                }
            },
            onClose = {
                affirmationViewModel.onNewAffirmationClose()
            }
        )
    }
}


@Composable
fun AffirmationCard(
    affirmationEntity: AffirmationEntity,
    onAffirmationCardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onAffirmationCardClicked() },
        elevation = 4.dp
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(affirmationEntity.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp)
            )

            Text(
                text = affirmationEntity.statement,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}
