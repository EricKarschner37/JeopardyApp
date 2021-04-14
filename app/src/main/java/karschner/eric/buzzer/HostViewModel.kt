package karschner.eric.buzzer

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.json.JSONObject

class HostViewModel() : ViewModel() {
    private val WS_HOST = "jeopardy.karschner.studio"
    private val WS_PATH = "ws/host"
    private val TAG = "HostViewModel"
    private val mainHandler = Handler()

    private val _stateName = MutableLiveData<String>()
    val stateName: LiveData<String> = _stateName
    private val _buzzersOpen = MutableLiveData<Boolean>()
    val buzzersOpen: LiveData<Boolean> = _buzzersOpen
    private val _clue = MutableLiveData<MainActivity.Clue>()
    val clue: LiveData<MainActivity.Clue> = _clue
    private val _player = MutableLiveData<String>()
    val player: LiveData<String> = _player
    private val _players = MutableLiveData<List<Pair<String, Int>>>()
    val players: LiveData<List<Pair<String, Int>>> = _players

    private lateinit var outgoing: SendChannel<Frame>

    init {
        val client = HttpClient(OkHttp) {
            install(WebSockets)
        }

        GlobalScope.launch {
            client.wss(
                method = HttpMethod.Get,
                host = WS_HOST,
                port = 443, path = WS_PATH
            ) {
                this@HostViewModel.outgoing = outgoing
                for (frame in incoming) {
                    val text = (frame as Frame.Text).readText()
                    val json = JSONObject(text)
                    if (json.getString("message") != "state") continue
                    mainHandler.post{
                        _buzzersOpen.value = json.getBoolean("buzzers_open")
                        _stateName.value = json.getString("name")
                        _clue.value = MainActivity.Clue(
                            json.getInt("cost"),
                            json.getString("clue"),
                            json.getString("response")
                        )
                        _player.value = json.getString("selected_player")
                        val newPlayers = mutableListOf<Pair<String, Int>>()
                        val playersJson = json.getJSONObject("players")
                        playersJson.keys().forEach {
                            Log.i(TAG, playersJson.getJSONObject(it).toString())
                            newPlayers.add(
                                Pair(it, playersJson.getJSONObject(it).getInt("Points"))
                            )
                        }
                        _players.value = newPlayers
                    }
                }
            }
        }
    }

    fun onBuzzerSwitched(checked: Boolean) {
        if (!::outgoing.isInitialized) return
        GlobalScope.launch {
            val msg = JSONObject()
            msg.put("request", if (checked) "open" else "close")
            outgoing.send(Frame.Text(msg.toString()))
        }
    }

    fun playerWrong() {
        if (!::outgoing.isInitialized) return
        GlobalScope.launch {
            val msg = JSONObject()
            msg.put("request", "correct")
            msg.put("correct", false)
            outgoing.send(Frame.Text(msg.toString()))
        }
    }

    fun playerRight() {
        if (!::outgoing.isInitialized) return
        GlobalScope.launch {
            val msg = JSONObject()
            msg.put("request", "correct")
            msg.put("correct", true)
            outgoing.send(Frame.Text(msg.toString()))
        }
    }

    fun choosePlayer(name: String) {
        if (!::outgoing.isInitialized) return
        GlobalScope.launch {
            val msg = JSONObject()
            msg.put("request", "player")
            msg.put("player", name)
            outgoing.send(Frame.Text(msg.toString()))
        }
    }
}