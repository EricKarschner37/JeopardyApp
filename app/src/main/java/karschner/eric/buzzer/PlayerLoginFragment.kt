package karschner.eric.buzzer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_player_login.*
import kotlinx.android.synthetic.main.fragment_player_login.view.*
import kotlinx.android.synthetic.main.fragment_player_login.view.player_name_edit_text

class PlayerLoginFragment : Fragment() {

    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player_login, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = activity!!.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if (prefs.contains("name")){
            view.player_name_edit_text.setText(prefs.getString("name", ""))
        }

        view.player_connect_button.setOnClickListener {
            val name = player_name_edit_text.text.toString()
            if (name.isValidPlayerName()){
                player_name_input.error = null

                prefs.edit()
                    .putString("name", name)
                    .apply()

                (activity!! as MainActivity).setFragment(PlayerFragment(name))
            } else {
                player_name_input.error = "Invalid name"
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun String.isValidPlayerName(): Boolean{
        return isNotBlank() and isNotEmpty() and (length < 24)
    }
}