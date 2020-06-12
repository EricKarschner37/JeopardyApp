package karschner.eric.buzzer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var prefs: SharedPreferences
    private lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        setSupportActionBar(app_bar)

        if (savedInstanceState == null){
            fragment = if (prefs.contains("host") and prefs.getBoolean("host", false)){
                HostFragment()
            } else {
                PlayerLoginFragment()
            }

            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        }

        host_switch.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                setFragment(HostFragment())
            } else {
                setFragment(PlayerLoginFragment())
            }
        }
    }

    fun setFragment(fragment: Fragment) {
        this.fragment = fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (fragment is PlayerFragment) {
            setFragment(PlayerLoginFragment())
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> drawer.openDrawer(settings_view)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
