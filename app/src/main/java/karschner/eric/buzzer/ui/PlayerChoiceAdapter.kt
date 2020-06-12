package karschner.eric.buzzer.ui

import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import karschner.eric.buzzer.Player
import karschner.eric.buzzer.R
import karschner.eric.buzzer.network.HostBuzzer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

class PlayerChoiceAdapter(private val players: Array<Player>, private val buzzer: HostBuzzer) : RecyclerView.Adapter<PlayerChoiceAdapter.ViewHolder>() {
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
                buzzer.choosePlayer(player.name)
            }
        }
    }
}