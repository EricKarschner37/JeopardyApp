package karschner.eric.buzzer.network

import android.provider.Settings
import android.util.Log
import karschner.eric.buzzer.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.Socket

const val HOST_URI = "wss://jeopardy.karschner.studio/ws/buzzer/host/"

class HostBuzzer(): Buzzer(HOST_URI) {
    override var TAG = "HostBuzzer"

    fun playerResponse(name: String, correct: Boolean){
        val json = JSONObject()
        json.put("request", "response")
        json.put("correct", correct)
        json.put("name", name)

        send(json.toString())
    }

    fun openBuzzers() {
        val json = JSONObject()
        json.put("request", "open")

        send(json.toString())
    }

    fun choosePlayer(name: String){
        val json = JSONObject()
        json.put("request", "player_choice")
        json.put("name", name)

        send(json.toString())
    }

    fun closeBuzzers(){
        val json = JSONObject()
        json.put("request", "close")

        send(json.toString())
    }

    override fun onTextReceived(message: String?) {
        message?.let{
            Log.i(TAG, it)
            notifyObservers(message)
        }
    }
}