package karschner.eric.buzzer.ui

import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import karschner.eric.buzzer.R
import karschner.eric.buzzer.network.HostBuzzer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

class PlayerChoiceAdapter(private val players: JSONArray, private val buzzer: HostBuzzer) : RecyclerView.Adapter<PlayerChoiceAdapter.ViewHolder>() {
    class ViewHolder(val btn: Button): RecyclerView.ViewHolder(btn)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val btn = LayoutInflater.from(parent.context).inflate(R.layout.player_choice_item, parent, false) as Button
        return ViewHolder(btn)
    }

    override fun getItemCount(): Int = players.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = players.getString(position)
        holder.btn.text = name
        holder.btn.setOnClickListener {
            GlobalScope.launch{
                buzzer.choosePlayer(name)
            }
        }
    }
}