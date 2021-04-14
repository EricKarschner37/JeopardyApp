package karschner.eric.buzzer.ui

import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import io.ktor.http.cio.websocket.*
import karschner.eric.buzzer.Player
import karschner.eric.buzzer.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class PlayerChoiceAdapter(private val players: Array<Player>, private val outgoing: SendChannel<Frame>) : RecyclerView.Adapter<PlayerChoiceAdapter.ViewHolder>() {
    class ViewHolder(val btn: Button): RecyclerView.ViewHolder(btn)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val btn = LayoutInflater.from(parent.context).inflate(R.layout.player_choice_item, parent, false) as Button
        return ViewHolder(btn)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.btn.text = player.name
        holder.btn.setOnClickListener {
            GlobalScope.launch{
                val msg = JSONObject()
                msg.put("request", "player")
                msg.put("player", player.name)
                outgoing.send(Frame.Text(msg.toString()))
            }
        }
    }
}