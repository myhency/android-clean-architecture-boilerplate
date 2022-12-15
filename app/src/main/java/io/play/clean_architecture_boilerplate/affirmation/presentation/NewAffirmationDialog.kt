package io.play.clean_architecture_boilerplate.affirmation.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewAffirmationDialog(
    onDismissButtonClicked: (String, String, String, String) -> Unit, onClose: () -> Unit
) {
    var affirmationStatement by remember {
        mutableStateOf("")
    }
    var todayFeeling by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Howdy!!", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    IconButton(onClick = { onClose() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
                Spacer(Modifier.size(18.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your affirmation")
                        },
                        value = affirmationStatement,
                        onValueChange = { affirmationStatement = it })
                    TextField(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 200.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(text = "How's your feeling today?")
                        },
                        value = todayFeeling,
                        onValueChange = { todayFeeling = it },
                        singleLine = false,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onDismissButtonClicked(
                                    affirmationStatement,
                                    todayFeeling,
                                    LocalDateTime.now().toString(),
                                    "https://picsum.photos/id/${(1..999).random()}/300/200"
                                )
                            }
                        ) {
                            Text(text = "Dismiss")
                        }
                    }
                }
            }
        }
    }
}