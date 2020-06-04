package karschner.eric.buzzer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.edit
import karschner.eric.buzzer.network.PlayerBuzzer
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class PlayerActivity : AppCompatActivity(), Observer{
    lateinit var playerBuzzer: PlayerBuzzer
    private val TAG = "PlayerActivity"
    private val mainHandler = Handler()
    lateinit var prefs: SharedPreferences
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        prefs = getSharedPreferences("main", Context.MODE_PRIVATE)

        if (prefs.contains("name")){
            name_et.setText(prefs.getString("name", "default"))
        }

        connect_btn.setOnClickListener {
            this.name = name_et.text.toString()
            prefs.edit { putString("name", name) }

            initBuzzer()
        }

        buzz_btn.setOnClickListener {
            GlobalScope.launch{
                playerBuzzer.buzzIn()
            }
        }
    }

    private fun showBuzzer(){
        hideAll()
        buzzer_layout.visibility = VISIBLE
    }

    private fun initBuzzer(){
        GlobalScope.launch {
            Log.i(TAG, "Creating buzzer")
            playerBuzzer = PlayerBuzzer(name)
            playerBuzzer.addObserver(this@PlayerActivity)

            Log.i(TAG, "Buzzer created")
        }
    }

    private fun showBuzzedIn(){
        hideAll()
        buzzer_layout.visibility = VISIBLE
        status_tv.text = "Buzzed in!"
    }

    private fun showBuzzedOut(){
        hideAll()
        buzzer_layout.visibility = VISIBLE
        status_tv.text = ""
    }

    private fun showQuestion(clue: String, cost: Int){
        hideAll()
        buzzer_layout.visibility = VISIBLE
        clue_tv.text = "Clue: $clue"
    }

    private fun showWager(){
        hideAll()
        wager_layout.visibility = VISIBLE

        wager_btn.setOnClickListener {
            val wager = wager_et.text.toString()
            if (wager.isNotEmpty()){
                GlobalScope.launch{
                    playerBuzzer.sendWager(wager.toInt())
                }
            }
        }
    }

    private fun hideAll(){
        buzzer_layout.visibility = GONE
        wager_layout.visibility = GONE
        connect_layout.visibility = GONE
    }

    override fun update(msg: String) {
        Log.i(TAG, msg)
        val json = JSONObject(msg)
        mainHandler.post{
            if (json.getString("message") == "state"){
                when (json.getString("state")){
                    "buzzed" -> if (json.getString("player") == name) showBuzzedIn() else showBuzzedOut()
                    "wager" -> if (json.getString("player") == name) showWager() else showBuzzedOut()
                    "end" -> onBackPressed()
                    "question" -> showQuestion(json.getString("clue"), json.getInt("cost"))
                }
            }
        }
    }

    override fun onPause() {
        playerBuzzer.close()
        super.onPause()
    }

    override fun onResume() {
        playerBuzzer = PlayerBuzzer(name)
        playerBuzzer.addObserver(this)
        Log.i(TAG, "onResume")
        super.onResume()
    }
}
