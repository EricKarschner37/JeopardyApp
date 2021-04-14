package karschner.eric.buzzer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        setSupportActionBar(app_bar)

        setContent { MaterialTheme {
            var host by remember { mutableStateOf(false)}
            var connected by remember { mutableStateOf(false) }
            var name by rememberSaveable { mutableStateOf(prefs.getString("name", "")!!) }

            when {
                host -> {
                    HostScreen()
                }
                connected -> {
                    PlayerScreen(PlayerViewModel(name))
                }
                else -> {
                    PlayerLoginScreen(name = name,
                        onNameChange = {name = it},
                        onConnectPress = {if (name.isValidName()) connected = true; prefs.edit { putString("name", name) }},
                        onHostPress = {host = true}
                    )
                }
            }
        }
    } }

    @Composable
    fun PlayerLoginScreen(name: String, onNameChange: (String) -> Unit, onConnectPress: () -> Unit, onHostPress: () -> Unit) {
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
    fun HostScreen(viewModel: HostViewModel = viewModel()) {
        val stateName: String by viewModel.stateName.observeAsState("board")
        val buzzersOpen: Boolean by viewModel.buzzersOpen.observeAsState(false)
        val clue: Clue by viewModel.clue.observeAsState(
            Clue(
                0,
                "",
                "",
            )
        )
        val player: String by viewModel.player.observeAsState(initial = "")
        val players: List<Pair<String, Int>> by viewModel.players.observeAsState(initial = listOf())

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Host",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.weight(1.0f))
            ClueDisplay(clue = clue)
            if (stateName == "clue") {
                Spacer(modifier = Modifier.weight(1.0f))
                if (player == "") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text(text = "Buzzers: ", style=MaterialTheme.typography.body2)
                        Switch(checked = buzzersOpen, viewModel::onBuzzerSwitched)
                    }
                } else {
                    Text(text = "Player buzzed in: $player")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = viewModel::playerWrong
                        ) {
                            Text(text = "WRONG", style=MaterialTheme.typography.body2)
                        }
                        Button(
                            onClick = viewModel::playerRight
                        ) {
                            Text(text = "CORRECT", style=MaterialTheme.typography.body2)
                        }
                    }
                }
            }
            if (stateName == "daily_double") {
                LazyColumn{
                    items(players) {
                        Button(
                            onClick = { viewModel.choosePlayer(it.first) }
                        ) {
                            Text(text = it.first)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PlayerScreen(viewModel: PlayerViewModel = viewModel()) {
        val buzzed: Boolean by viewModel.buzzed.observeAsState(false)
        val clue: Clue by viewModel.clue.observeAsState(
            Clue(
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
    private fun ClueDisplay(clue: Clue) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = if (clue.cost == 0) "" else "$${clue.cost}")
            Text(text = clue.clue)
            Text(text = clue.response)
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

    fun String.isValidName(): Boolean {
        return !this.contains(" ") && this.isNotEmpty() && this.isNotBlank()
    }

    data class Clue(
        val cost: Int,
        val clue: String,
        val response: String,
    )
}
