package com.smh_digitalassistant_library.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.fragments.CalendarFragment
import com.smh_digitalassistant_library.fragments.ChatFragment
import com.smh_digitalassistant_library.fragments.DailyTasksFragment
import com.smh_digitalassistant_library.fragments.HealthRegFragment
import com.smh_digitalassistant_library.settings.SharedPreference
import com.smh_digitalassistant_library.utils.Utility
import kotlinx.android.synthetic.main.activity_patient.*

class PatientActivity : AppCompatActivity() {
    private var userId: String = ""
    private var patientId: String = ""
    var name: String = ""

    private var notificationManager: NotificationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)
        userId = intent.getStringExtra("UID").toString()
        patientId = intent.getStringExtra("PatientId").toString()
        name = intent.getStringExtra("Username").toString()
        val gender = intent.getStringExtra("Gender").toString()
        val mLastClickTime = 0

        //MainActivity().finish()

        if (gender == "M") {
            greetTitleP.text = ("Bem vindo de volta $name")
        } else {
            greetTitleP.text = ("Bem vinda de volta $name")
        }

        buttonChat.setOnClickListener { view ->
            Utility.preventOverclick(view)
            val fragment1 = ChatFragment()
            val bundle = Bundle().apply {
                putString("patientID", userId)
                putBoolean("TaskSelected", false)
                putString("name", name)
            }
            fragment1.arguments = bundle
            extendFragment(fragment1, "Chat")
        }

        buttonDailyTasksP.setOnClickListener {view ->
            Utility.preventOverclick(view)
            val fragment2 = DailyTasksFragment()
            val bundle = Bundle().apply {
                putString("patientID", patientId)
            }
            fragment2.arguments = bundle
            extendFragment(fragment2, "DailyTasks")
        }

        buttonCalendarP.setOnClickListener { view ->
            Utility.preventOverclick(view)
            val fragment3 = CalendarFragment()
            val bundle = Bundle().apply {
                putString("patientID", patientId)
            }
            fragment3.arguments = bundle
            extendFragment(fragment3, "Calendar")
        }

        buttonHealthRegP.setOnClickListener { view ->
            Utility.preventOverclick(view)
            extendFragment(HealthRegFragment(), "HealthReg")
        }

        buttonExitSessionP.setOnClickListener {
            SharedPreference.setRememberLogin(this, "FALSE")
            finish()
        }

        buttonExitAppP.setOnClickListener {
            finishAffinity()
        }
    }

    fun openChatOn(position: Int, tname: String) {
        val fragment1 = ChatFragment()
        val bundle = Bundle().apply {
            putString("patientID", userId)
            putBoolean("taskSelected", true)
            putString("name", name)
            putInt("position", position)
            putString("taskName", tname)
        }
        fragment1.arguments = bundle
        extendFragment(fragment1, "Chat")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun extendFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_up, R.anim.enter_up, R.anim.exit_up, R.anim.exit_up)
        ft.replace(R.id.patient_fragment_container, fragment, tag).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("Chat")?.isVisible == true
            || supportFragmentManager.findFragmentByTag("DailyTasks")?.isVisible == true
            || supportFragmentManager.findFragmentByTag("Calendar")?.isVisible == true
            || supportFragmentManager.findFragmentByTag("HealthReg")?.isVisible == true) {
            supportFragmentManager.popBackStackImmediate()
        }
    }
}