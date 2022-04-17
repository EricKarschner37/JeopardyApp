package karschner.eric.buzzer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import karschner.eric.buzzer.network.Games
import karschner.eric.buzzer.ui.ClueDisplay
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(){

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jeopardy.karschner.studio/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val gamesService = retrofit.create(Games::class.java)

        setSupportActionBar(app_bar)

        setContent { MaterialTheme {
            var host by remember { mutableStateOf(false)}
            var connected by remember { mutableStateOf(false) }
            var name by rememberSaveable { mutableStateOf(prefs.getString("name", "")!!) }
            var gameNum by remember { mutableStateOf<Int?>(null)}
            var gameNums by remember { mutableStateOf(listOf<Int>())}

            gamesService.getGames().enqueue( object: Callback<List<Int>> {
                override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                    Log.i("MainActivity", response.body()?.toString().orEmpty())
                    response.body()?.let { gameNums = it }
                }

                override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                    Log.i("MainActivity", t.localizedMessage.orEmpty())
                }
            })

            when {
                host && gameNum !== null -> {
                    HostScreen(gameNum!!)
                }
                host -> {
                    HostLoginScreen(gameNums, onGameSelect = {gameNum = it})
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

    private fun String.isValidName(): Boolean {
        return !contains(" ") && isNotEmpty() && isNotBlank()
    }

    data class Clue(
        val cost: Int,
        val clue: String,
        val response: String,
    )
}
