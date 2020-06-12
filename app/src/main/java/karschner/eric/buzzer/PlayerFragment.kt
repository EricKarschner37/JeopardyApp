package karschner.eric.buzzer

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import karschner.eric.buzzer.network.PlayerBuzzer
import kotlinx.android.synthetic.main.buzz_view.view.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.fragment_player.view.connecting_layout
import kotlinx.android.synthetic.main.question_view.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class PlayerFragment(private val name: String): Fragment(), Observer {
    private lateinit var playerBuzzer: PlayerBuzzer
    private val TAG = "PlayerFragment"
    private val mainHandler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBuzzer()

        view.player_name.text = name

        view.buzz_layout.buzz_btn.setOnClickListener {
            GlobalScope.launch {
                playerBuzzer.buzzIn()
            }
        }
    }

    private fun initBuzzer() {
        showConnecting()
        playerBuzzer = PlayerBuzzer(name)
        playerBuzzer.addObserver(this)
    }

    private fun showConnecting() {
        connecting_layout.visibility = View.VISIBLE
    }

    private fun showConnected() {
        connecting_layout.visibility = View.GONE
    }

    private fun showBuzzedIn() {
        view?.let {
            it.buzz_layout.buzzed_status.text = getString(R.string.buzzed_in)
            it.buzz_layout.buzz_btn.isActivated = false
        }
    }

    private fun showBuzzedOut() {
        view?.let {
            it.buzz_layout.buzzed_status.text = getString(R.string.not_buzzed_in)
            it.buzz_layout.buzz_btn.isActivated = true
        }
    }

    private fun showQuestion(question: Question) {
        view?.let {
            it.question.clue.text = question.clue
            it.question.answer.text = ""
        }
    }

    private fun showAnswer(question: Question) {
        view?.let {
            it.question.clue.text = question.clue
            it.question.answer.text = question.answer
        }
    }

    private fun promptWager() {

    }

    override fun update(msg: String) {
        Log.i(TAG, msg)
        val json = JSONObject(msg)
        mainHandler.post {
            if (json.getString("message") == "state") {
                showConnected()
                val state = State.fromJson(json)
                when (state.state) {
                    "buzzed" -> showBuzzedIn()
                    "question" -> showQuestion(state.question)
                    "answer" -> showAnswer(state.question)
                    "daily_double" -> if (state.player == name) promptWager()
                }
            }
            else if (json.getString("message") == "end") {
                initBuzzer()
            }
        }
    }

    override fun onPause() {
        playerBuzzer.close()
        super.onPause()
    }

    override fun onResume() {
        initBuzzer()
        super.onResume()
    }
}