package karschner.eric.buzzer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        host_btn.setOnClickListener {
            val intent = Intent(this, HostActivity::class.java)
            startActivity(intent)
        }

        player_btn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
    }
}
