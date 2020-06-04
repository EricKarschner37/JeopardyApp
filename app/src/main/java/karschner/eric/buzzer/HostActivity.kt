package karschner.eric.buzzer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import karschner.eric.buzzer.network.HostBuzzer
import karschner.eric.buzzer.ui.PlayerChoiceAdapter
import kotlinx.android.synthetic.main.activity_host.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HostActivity : AppCompatActivity(), Observer {
    lateinit var hostBuzzer: HostBuzzer
    private val mainHandler = Handler()
    private val TAG = "HostActivity"
    lateinit var prefs: SharedPreferences
    lateinit var player: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        prefs = getSharedPreferences("main", Context.MODE_PRIVATE)

        submit_btn.setOnClickListener {
            initBuzzer()
        }

        open_btn.setOnClickListener {
            GlobalScope.launch{
                hostBuzzer.openBuzzers()
            }
        }

        close_btn.setOnClickListener {
            GlobalScope.launch{
                hostBuzzer.closeBuzzers()
            }
        }

        right_btn.setOnClickListener {
            GlobalScope.launch{
                hostBuzzer.playerResponse(player, true)
            }
        }

        wrong_btn.setOnClickListener {
            GlobalScope.launch{
                hostBuzzer.playerResponse(player, false)
            }
        }
    }

    fun initBuzzer(){
        GlobalScope.launch{
            hostBuzzer = HostBuzzer()
            hostBuzzer.addObserver(this@HostActivity)
        }
    }

    fun showBuzzersOpen(){
        hideAll()
        host_status_tv.text = "Buzzers are open!"
        host_layout.visibility = View.VISIBLE

        open_btn.visibility = View.GONE
        close_btn.visibility = View.VISIBLE
    }

    fun playerBuzzed(name: String){
        hideAll()

        this.player = name
        host_status_tv.text = "Player buzzed in: $name"

        host_layout.visibility = View.VISIBLE
        player_buzzed_layout.visibility = View.VISIBLE


        open_btn.visibility = View.GONE
        close_btn.visibility = View.GONE
    }

    fun showBuzzersClosed(){
        hideAll()
        host_layout.visibility = View.VISIBLE

        host_status_tv.text = "Buzzers are closed"
        open_btn.visibility = View.VISIBLE
        close_btn.visibility = View.GONE
    }

    fun showQuestion(clue: String, answer: String, cost: Int, open: Boolean){
        hideAll()

        host_layout.visibility = View.VISIBLE
        clue_tv.text = "Clue: $clue"
        answer_tv.text = "Answer: $answer"

        if (open){
            showBuzzersOpen()
        } else {
            showBuzzersClosed()
        }
    }

    fun showWagerLayout(players: JSONObject){
        hideAll()

        host_player_choice_rv.visibility = View.VISIBLE
        host_player_choice_rv.layoutManager = LinearLayoutManager(this)
        host_player_choice_rv.adapter = PlayerChoiceAdapter(players.names()!!, hostBuzzer)
    }

    fun hideAll(){
        host_layout.visibility = View.GONE
        host_connect_layout.visibility = View.GONE
        host_player_choice_rv.visibility = View.GONE
        player_buzzed_layout.visibility = View.GONE
    }

    override fun update(msg: String){
        Log.i(TAG, msg)
        val json = JSONObject(msg)
        mainHandler.post{
            if (json.getString("message") == "state"){
                when (json.getString("state")){
                    "buzzed" -> playerBuzzed(json.getString("player"))
                    "question" -> showQuestion(json.getString("clue"), json.getString("answer"), json.getInt("cost"), json.getBoolean("buzzers_open"))
                    "daily_double" -> showWagerLayout(json.getJSONObject("players"))
                    "end" -> onBackPressed()
                }
            }
        }
    }

    override fun onPause(){
        hostBuzzer.close()
        super.onPause()
    }

    override fun onResume() {
        hostBuzzer = HostBuzzer()
        hostBuzzer.addObserver(this)
        super.onResume()
    }
}
