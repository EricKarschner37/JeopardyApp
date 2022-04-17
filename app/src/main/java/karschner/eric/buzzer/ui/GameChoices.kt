package karschner.eric.buzzer.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun GameChoices(gameNums: List<Int>?, onGameSelect: (Int) -> Unit) {
    if (gameNums == null) {
        return CircularProgressIndicator()
    }

    LazyColumn{
        items(gameNums.sorted()) {
            Button(
                onClick = { onGameSelect(it) }
            ) {
                Text(text = "Game #$it")
            }
        }
    }
}