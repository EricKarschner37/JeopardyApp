package karschner.eric.buzzer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import karschner.eric.buzzer.MainActivity

@Composable
fun ClueDisplay(clue: MainActivity.Clue) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = if (clue.cost == 0) "" else "$${clue.cost}")
        Text(text = clue.clue)
        Text(text = clue.response)
    }
}
