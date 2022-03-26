package karschner.eric.buzzer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel

class BoardTvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Board()
        }
    }

    @Composable
    fun Board(viewModel: BoardTvViewModel = viewModel()) {
        val focusRequester = FocusRequester()
        Row {
            ClueSquare(
                focused = viewModel.focused == Position(0, 0),
                modifier = Modifier
                    .focusable()
                    .focusRequester(focusRequester)
                    .onFocusChanged { Log.i("BoardTvActivity", it.hasFocus.toString());viewModel.focused = if (it.isFocused) Position(0, 0) else viewModel.focused }
                    .focusTarget()
                    .pointerInput(Unit) { detectTapGestures { focusRequester.requestFocus() }}
            )
            ClueSquare(
                focused = viewModel.focused == Position(0, 1),
                modifier = Modifier
                    .focusable()
                    .focusRequester(focusRequester)
                    .onFocusChanged { Log.i("BoardTvActivity", it.isFocused.toString());viewModel.focused = if (it.isFocused) Position(0, 1) else viewModel.focused }
                    .focusTarget()
                    .pointerInput(Unit) { detectTapGestures { focusRequester.requestFocus() }}
            )
        }
    }

    @Composable
    fun ClueSquare(focused: Boolean, modifier: Modifier = Modifier) {
        Text(modifier = modifier, text = if (focused) "Focused!" else "Clue")
    }
}