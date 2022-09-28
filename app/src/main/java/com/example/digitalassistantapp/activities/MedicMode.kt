package com.example.digitalassistantapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.fragments.*
import com.example.digitalassistantapp.settings.SharedPreference
import com.example.digitalassistantapp.utils.Utility
import kotlinx.android.synthetic.main.activity_medic_mode.*

class MedicMode : AppCompatActivity() {
    var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medic_mode)
        userId = intent.getStringExtra("UID").toString()
        val medicId = intent.getStringExtra("MedicId")
        val name = intent.getStringExtra("Username")
        val gender = intent.getStringExtra("Gender").toString()

        MainActivity().finish()

        if (gender == "M") {
            greetTitleM.text = ("Bem vindo de volta Dr. $name")
        } else {
            greetTitleM.text = ("Bem vinda de volta Dra. $name")
        }

        buttonPatientListM.setOnClickListener { view ->
            Utility.preventOverclick(view)
            val fragment1 = PatientListFragment()
            val bundle = Bundle().apply {
                putString("MedicId", medicId)
            }
            fragment1.arguments = bundle
            extendFragment(fragment1, "PatientList")
        }

        buttonTaskListM.setOnClickListener { view ->
            Utility.preventOverclick(view)
            extendFragment(TaskListFragment(), "TaskList")
        }

        buttonRegister.setOnClickListener { view ->
            Utility.preventOverclick(view)
            val fragment3 = RegisterFragment()
            val bundle = Bundle().apply {
                putString("MedicId", medicId)
            }
            fragment3.arguments = bundle
            extendFragment(fragment3, "Register")
        }

        buttonExitSessionM.setOnClickListener {
            SharedPreference.setRememberLogin(this, "FALSE")
            finish()
        }

        buttonExitAppM.setOnClickListener {
            finishAffinity()
        }
    }

    fun handlePatient(id: String, name: String, picture: Int) {
        val fragment4 = PatientHandleFragment()
        val bundle = Bundle().apply {
            putString("patientID", id)
            putString("patientName", name)
            putString("userId", userId)
            putInt("patientPic", picture)
        }
        fragment4.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.medic_fragment_container, fragment4, "PatientHandle").addToBackStack(null).commit()
    }

    private fun extendFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()

        ft.setCustomAnimations(R.anim.enter_up, R.anim.enter_up, R.anim.exit_up, R.anim.exit_up)
        ft.replace(R.id.medic_fragment_container, fragment, tag).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("PatientList")?.isVisible == true
            || supportFragmentManager.findFragmentByTag("TaskList")?.isVisible == true
            || supportFragmentManager.findFragmentByTag("Register")?.isVisible == true
            || supportFragmentManager.findFragmentByTag("PatientHandle")?.isVisible == true){
            supportFragmentManager.popBackStackImmediate()
        }
    }
}