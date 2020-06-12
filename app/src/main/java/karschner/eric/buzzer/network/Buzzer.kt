package karschner.eric.buzzer.network

import android.util.Log
import androidx.core.net.toUri
import karschner.eric.buzzer.Observer
import org.json.JSONObject
import tech.gusavila92.websocketclient.WebSocketClient
import java.lang.Exception
import java.net.URI

abstract class Buzzer(uri: String): WebSocketClient(URI(uri)) {
    val observers: MutableList<Observer> = mutableListOf()
    open var TAG = "Buzzer"

    override fun onBinaryReceived(data: ByteArray?) {}

    override fun onCloseReceived() {
        val json = JSONObject()
        json.put("message", "end")
        notifyObservers(json.toString())
    }

    override fun onException(e: Exception?) {
        Log.e(TAG, e?.message)
        val json = JSONObject()
        json.put("message", "end")
        notifyObservers(json.toString())
    }

    override fun onPingReceived(data: ByteArray?) {}

    override fun onPongReceived(data: ByteArray?) {}

    override fun onOpen() {}

    override fun send(message: String?) {
        Log.i(TAG, "Sending: $message")
        super.send(message)
    }

    fun notifyObservers(msg: String){
        observers.forEach { it.update(msg) }
    }

    fun addObserver(o: Observer){
        observers.add(o)
    }

    init {
        setConnectTimeout(1000)
        setReadTimeout(60000)
        enableAutomaticReconnection(10)
        connect()
    }
}