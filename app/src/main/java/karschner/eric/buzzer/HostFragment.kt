package karschner.eric.buzzer

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.GsonBuilder
import karschner.eric.buzzer.network.HostBuzzer
import karschner.eric.buzzer.ui.PlayerChoiceAdapter
import kotlinx.android.synthetic.main.buzzer_switch_view.view.*
import kotlinx.android.synthetic.main.fragment_host.view.*
import kotlinx.android.synthetic.main.player_buzzed_view.view.*
import kotlinx.android.synthetic.main.question_view.view.*
import org.json.JSONObject

class HostFragment : Fragment(), Observer {
    private lateinit var hostBuzzer: HostBuzzer
    private val mainHandler = Handler()
    private val TAG = "HostFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBuzzer()

        view.buzzer_layout.buzzer_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hostBuzzer.openBuzzers()
            } else {
                hostBuzzer.closeBuzzers()
            }
        }

        view.player_buzzed.right_btn.setOnClickListener {
            hostBuzzer.playerResponse(true)
        }

        view.player_buzzed.wrong_btn.setOnClickListener {
            hostBuzzer.playerResponse(false)
        }

        view.player_choice_rv.layoutManager = GridLayoutManager(activity!!, 2)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_host,
        container,
        false)


    fun initBuzzer() {
        hostBuzzer = HostBuzzer()
        hostBuzzer.addObserver(this)
    }

    fun showPlayerBuzzed(name: String){
        view?.let {
            it.player_buzzed.player.text = name
            it.player_buzzed.visibility = View.VISIBLE
        }
    }

    fun showQuestion(question: Question) {
        view?.let {
            it.question.clue.text = question.clue
            it.question.answer.text = question.answer
            it.buzzer_layout.visibility = View.VISIBLE
        }
    }

    fun showIdle() {
        view?.let {
            it.buzzer_layout.visibility = View.GONE
        }
    }

    fun showChoosePlayer(players: Array<Player>) {
        view?.let {
            it.player_choice_rv.visibility = View.VISIBLE
            it.player_choice_rv.adapter = PlayerChoiceAdapter(players, hostBuzzer)
        }
    }

    override fun update(msg: String) {
        Log.i(TAG, msg)
        val json = JSONObject(msg)
        mainHandler.post {
            if (json.getString("message") == "state") {
                val state = State.fromJson(json)
                when (state.state) {
                    "buzzed" -> showPlayerBuzzed(state.player)
                    "question" -> showQuestion(state.question)
                    "idle" -> showIdle()
                    "daily_double" -> showChoosePlayer(state.players)
                }
            } else if (json.getString("message") == "end") {
                initBuzzer()
            }
        }
    }

    override fun onPause() {
        hostBuzzer.close()
        super.onPause()
    }

    override fun onResume() {
        initBuzzer()
        super.onResume()
    }
}