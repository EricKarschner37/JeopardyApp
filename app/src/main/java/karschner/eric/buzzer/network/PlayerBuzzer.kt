package karschner.eric.buzzer.network

import android.util.Log
import org.json.JSONObject

const val PLAYER_URI = "wss://jeopardy.karschner.studio/ws/buzzer/"

class PlayerBuzzer(val name: String = "test"): Buzzer(PLAYER_URI) {
    override var TAG = "Buzzer"

    fun buzzIn() {
        val json = JSONObject()
        json.put("request", "buzz")

        send(json.toString())
    }

    fun sendWager(amount: Int) {
        val json = JSONObject()
        json.put("request", "wager")
        json.put("amount", amount)

        send(json.toString())
    }

    override fun onTextReceived(message: String?) {
        message?.let{
            Log.i(TAG, it)
            notifyObservers(it)
        }
    }

    override fun onOpen() {
        val json = JSONObject()
        json.put("request", "register")
        json.put("name", name)

        if (name != ""){
            send(json.toString())
        }
    }
}