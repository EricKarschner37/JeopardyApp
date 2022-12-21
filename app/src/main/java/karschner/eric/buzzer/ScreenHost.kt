package karschner.eric.buzzer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import karschner.eric.buzzer.ui.ClueDisplay
import karschner.eric.buzzer.ui.GameChoices


@Composable
fun HostScreen(gameNum: Int) {
    val viewModel: HostViewModel = viewModel(factory=object: ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>):T = HostViewModel(gameNum) as T
    })
    val stateName: String by viewModel.stateName.observeAsState("Board")
    val buzzersOpen: Boolean by viewModel.buzzersOpen.observeAsState(false)
    val clue: MainActivity.Clue by viewModel.clue.observeAsState(
        MainActivity.Clue(
            0,
            "",
            "",
        )
    )
    val selectedPlayer: String by viewModel.player.observeAsState(initial = "")
    val players: List<Player> by viewModel.players.observeAsState(initial = listOf())

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
        Spacer(modifier = Modifier.weight(1.0f))
        when (stateName) {
            "Clue" -> BuzzerControl(
                selectedPlayer,
                buzzersOpen,
                viewModel::onBuzzerSwitched,
                viewModel::playerRight,
                viewModel::playerWrong
            )
            "DailyDouble" -> PlayerChoices(players) { player -> viewModel.choosePlayer(player.name) }
        }
    }
}

@Composable
private fun BuzzerControl(selectedPlayer: String, buzzersOpen: Boolean, onBuzzerToggle: (Boolean) -> Unit, onPlayerRight: () -> Unit, onPlayerWrong: () -> Unit) {
    if (selectedPlayer == "") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Buzzers: ", style= MaterialTheme.typography.body2)
            Switch(checked = buzzersOpen, onBuzzerToggle)
        }
    } else {
        Text(text = "Player buzzed in: $selectedPlayer")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = onPlayerWrong
            ) {
                Text(text = "WRONG", style= MaterialTheme.typography.body2)
            }
            Button(
                onClick = onPlayerRight
            ) {
                Text(text = "CORRECT", style= MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
private fun PlayerChoices(players: List<Player>, onPlayerSelect: (Player) -> Unit) {
    LazyColumn {
        items(players) {
            Button(
                onClick = { onPlayerSelect(it) }
            ) {
                Text(text = it.name)
            }
        }
    }
}

@Composable
fun HostLoginScreen(gameNums: List<Int>, onGameSelect: (Int) -> Unit) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text= "Jeopardy!",
            modifier = Modifier.padding(bottom = 32.dp),
            style = MaterialTheme.typography.h3
        )
        Text(
            text = "Welcome, host! Please select the game number shown on the board.",
            modifier = Modifier.padding(bottom = 128.dp),
            style = MaterialTheme.typography.subtitle1
        )
        GameChoices(gameNums, onGameSelect)
    }
}
