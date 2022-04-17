package karschner.eric.buzzer

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import karschner.eric.buzzer.ui.ClueDisplay

@Composable
fun PlayerScreen(viewModel: PlayerViewModel = viewModel()) {
    val buzzed: Boolean by viewModel.buzzed.observeAsState(false)
    val clue: MainActivity.Clue by viewModel.clue.observeAsState(
        MainActivity.Clue(
            0,
            "",
            ""
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = viewModel.name,
            style=MaterialTheme.typography.h3
        )
        Spacer(modifier = Modifier.weight(1.0f))
        ClueDisplay(clue = clue)
        Spacer(modifier = Modifier.weight(1.0f))
        BuzzerDisplay(buzzed = buzzed, viewModel::buzz)
    }
}

@Composable
fun Activity.PlayerLoginScreen(name: String, onNameChange: (String) -> Unit, onConnectPress: () -> Unit, onHostPress: () -> Unit) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Jeopardy!",
            modifier = Modifier.padding(bottom = 32.dp),
            style = MaterialTheme.typography.h3
        )
        Text(
            text = "Welcome to Jeopardy! What's your name?",
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.weight(1.0f))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {currentFocus?.clearFocus()})
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onHostPress) {
                Text(
                    text = "HOST",
                    style = MaterialTheme.typography.button
                )
            }
            Button(onClick = onConnectPress) {
                Text(
                    text = "CONNECT",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@Composable
private fun BuzzerDisplay(buzzed: Boolean, buzz: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (buzzed) Text(text = "You are buzzed in!")
        else Text(text = "You are not buzzed in")
        Spacer(modifier = Modifier.size(12.dp))
        Button(
            onClick = buzz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Buzz")
        }
    }
}