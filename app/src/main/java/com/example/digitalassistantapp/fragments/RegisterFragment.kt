package com.example.digitalassistantapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.utils.Constants.API_ADDRESS
import com.example.digitalassistantapp.utils.EncryptionEngine
import com.example.digitalassistantapp.utils.Utility.apipost
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.*

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textUserCreated.visibility = View.INVISIBLE

        back_buttonR.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }

        if (editTextPersonNameR.isNotEmpty() && editTextSurnameR.isNotEmpty() && editTextPatientIdR.isNotEmpty() && editTextPasswordR.isNotEmpty()) {
            val firstName = editTextPersonNameR.editText!!.text.toString()
            val lastName = editTextSurnameR.editText!!.text.toString()
            val patientId = editTextPatientIdR.editText!!.text.toString()
            val password = EncryptionEngine.encryptsha1(editTextPasswordR.editText!!.text.toString())

            CoroutineScope(Dispatchers.Main).launch{
                val res = async(Dispatchers.IO){insertPatient(0, firstName, lastName, password)}
            }
        }
    }

    private fun insertPatient(loginToken: Int, firstName: String, lastName: String, password: String) {
        val body = """{ "loginToken": $loginToken, "firstName": $firstName, "lastName": $lastName, "password": $password }"""
        val response = apipost("$API_ADDRESS/newUser", body)
    }

}