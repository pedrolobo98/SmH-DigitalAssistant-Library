package com.example.digitalassistantapp.activities

import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentTransaction
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.fragments.LoginFragment
import com.example.digitalassistantapp.settings.SharedPreference
import com.example.digitalassistantapp.utils.Utility
import kotlinx.android.synthetic.main.activity_main.*
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLogin.setOnClickListener { view ->
            Utility.preventOverclick(view)
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(R.anim.enter_down, R.anim.enter_down, R.anim.exit_down, R.anim.exit_down)
            ft.replace(R.id.fragment_container, LoginFragment()).addToBackStack(null).commit()
        }

        buttonExit.setOnClickListener {
            finishAffinity()
        }

        imageViewLogos.setOnClickListener {
            about()
        }

        if (SharedPreference.getRememberLogin(this) == "TRUE") {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(R.anim.enter_down, R.anim.enter_down, R.anim.exit_down, R.anim.exit_down)
            ft.replace(R.id.fragment_container, LoginFragment()).addToBackStack(null).commit()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.popBackStackImmediate()
    }

    private fun about() {
        val builder = AlertDialog.Builder(this)
        val ipInput = ImageView(this)

        builder.setTitle("Sobre a aplicação")
        builder.setMessage(R.string.app_about)
        ipInput.setImageResource(R.drawable.mainlogos)
        ipInput.adjustViewBounds = true
        ipInput.setPadding(50, 50, 50, 0)
        builder.setView(ipInput)

        builder.setNegativeButton("Fechar") {_, _ ->}

        builder.create().show()
    }

}