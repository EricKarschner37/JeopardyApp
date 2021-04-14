package karschner.eric.buzzer

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


class PlayerViewModel(val name: String) : ViewModel() {
    private val WS_HOST = "jeopardy.karschner.studio"
    private val WS_PATH = "ws/buzzer"
    private val TAG = "PlayerViewModel"
    private val mainHandler = android.os.Handler()

    private val _clue = MutableLiveData<MainActivity.Clue>()
    val clue: LiveData<MainActivity.Clue> = _clue
    private val _buzzed = MutableLiveData<Boolean>()
    val buzzed: LiveData<Boolean> = _buzzed

    lateinit var outgoing: SendChannel<Frame>

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
                val registerMsg = JSONObject()
                registerMsg.put("request", "register")
                registerMsg.put("name", name)
                send(Frame.Text(registerMsg.toString()))
                this@PlayerViewModel.outgoing = outgoing
                for (frame in incoming) {
                    val text = (frame as Frame.Text).readText()
                    Log.i(TAG, text)
                    val json = JSONObject(text)
                    if (json.getString("message") != "state") continue
                    val response =
                        if (json.getString("name") == "clue"
                            || json.getString("name") == "daily_double") ""
                        else json.getString("response")

                    mainHandler.post(kotlinx.coroutines.Runnable {
                        _buzzed.value = json.getString("name") == "clue"
                                && json.getString("selected_player") == name
                        _clue.value = MainActivity.Clue(
                            json.getInt("cost"),
                            json.getString("clue"),
                            response
                        )
                    })
                }
            }
        }
    }

    fun buzz() {
        if (::outgoing.isInitialized) {
            GlobalScope.launch {
                val msg = JSONObject()
                msg.put("request", "buzz")
                outgoing.send(Frame.Text(msg.toString()))
            }
        }
    }
}