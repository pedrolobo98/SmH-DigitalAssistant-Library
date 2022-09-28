package com.example.digitalassistantapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.activities.MedicMode
import com.example.digitalassistantapp.activities.PatientMode
import com.example.digitalassistantapp.settings.SecureAppPrefs
import com.example.digitalassistantapp.settings.SharedPreference
import com.example.digitalassistantapp.utils.Constants
import com.example.digitalassistantapp.utils.Constants.ACCESS_TOKEN
import com.example.digitalassistantapp.utils.Constants.API_ADDRESS
import com.example.digitalassistantapp.utils.EncryptionEngine
import com.example.digitalassistantapp.utils.Utility
import com.example.digitalassistantapp.utils.Utility.apiget
import com.example.digitalassistantapp.utils.Utility.hideKeyboard
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import org.json.JSONArray

class LoginFragment : Fragment() {
    private lateinit var loading: ProgressBar
    private lateinit var uid: TextInputLayout
    private lateinit var passwd: TextInputLayout

    data class Credentials(val uid: Int, val pmId: Int, val loginToken: String, val name: String, val password: String, val session: String, val gender: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = requireView().findViewById(R.id.editTextPersonNameL)
        passwd = requireView().findViewById(R.id.editTextPasswordL)
        loading = view.findViewById(R.id.loginLoading)
        uid.editText?.isEnabled = true
        passwd.editText?.isEnabled = true
        buttonLoginL.text = getString(R.string.next)
        login_error_text.visibility = View.INVISIBLE

        if (SharedPreference.getRememberLogin(requireContext()) == "TRUE") {
            loading.visibility = View.VISIBLE
            buttonLoginL.text = getString(R.string.wait)
            uid.editText?.isEnabled = false
            passwd.editText?.isEnabled = false
            checkAutoLogin()
        }

        buttonLoginL.setOnClickListener { view ->
            Utility.preventOverclick(view)
            loading.visibility = View.VISIBLE
            buttonLoginL.text = getString(R.string.wait)
            userLogin()
            /*if(!SecureAppPrefs(requireContext()).securePreferences.getString("OAuthToken", null).isNullOrEmpty()){
                userLogin()
            } else {
                getToken()
            }*/
            //startMode(0, "Pedro", 1) //Debug Purposes
            hideKeyboard()
        }

        editTextPersonNameL.editText?.doOnTextChanged { _, _, _, _ ->
            login_error_text.visibility = View.INVISIBLE
        }

        editTextPasswordL.editText?.doOnTextChanged { _, _, _, _ ->
            login_error_text.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharedPreference.getRememberLogin(requireContext()) == "FALSE") {
            login_error_text.visibility = View.INVISIBLE
            uid.editText?.isEnabled = true
            passwd.editText?.isEnabled = true
            buttonLoginL.text = getString(R.string.next)
        }
    }

    private fun checkAutoLogin(){
        if (SharedPreference.getUserId(requireContext())?.length  != 0) {
            val data = SecureAppPrefs(requireContext()).securePreferences.getString("LoginData", null)
            if (!data.isNullOrEmpty()) {
                val separated = data.split("#")
                CoroutineScope(Dispatchers.Main).launch {
                    val credentials = async(Dispatchers.IO) {fetchCredentials(separated[0].toInt())}
                    if (separated[1] == credentials.await().password) {
                        startMode(credentials.await().loginToken, credentials.await().name, credentials.await().uid, credentials.await().pmId, credentials.await().gender)
                    } else {
                        login_error_text.text = "Erro de credenciais, entre manualmente"
                        login_error_text.visibility = View.VISIBLE
                        buttonLoginL.text = getString(R.string.next)
                        loading.visibility = View.INVISIBLE
                    }
                }
            } else {
                login_error_text.text = "Erro de credenciais, entre manualmente"
                login_error_text.visibility = View.VISIBLE
                buttonLoginL.text = getString(R.string.next)
                loading.visibility = View.INVISIBLE
            }
        }
    }

    //Get database API token
    private fun getToken() {
        val qrfragment = QRFragment()
        extendFragment(qrfragment, "qrfragment")
    }

    private fun extendFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_up, R.anim.enter_up, R.anim.exit_up, R.anim.exit_up)
        ft.replace(R.id.patient_fragment_container, fragment, tag).addToBackStack(null).commit()
    }

    private fun userLogin() {
        if (!uid.editText?.text.isNullOrEmpty() && !passwd.editText?.text.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                val credentials = async(Dispatchers.IO) { fetchCredentials(uid.editText?.text.toString().toInt()) }

                if (handlePassword(credentials.await().password)) {
                    startMode(credentials.await().loginToken, credentials.await().name, credentials.await().uid, credentials.await().pmId, credentials.await().gender)
                    if (rememberLogin.isChecked) {
                        SharedPreference.setRememberLogin(requireContext(), "TRUE")
                        SharedPreference.setUserId(requireContext(), credentials.await().uid.toString())
                        val loginDataSet = credentials.await().uid.toString() + "#" + credentials.await().password
                        with(SecureAppPrefs(requireContext()).securePreferences.edit()) {
                            putString("LoginData", loginDataSet)
                            apply()
                        }
                    } else {
                        SharedPreference.setRememberLogin(requireContext(), "FALSE")
                    }
                } else {
                    login_error_text.text = getString(R.string.no_password)
                    login_error_text.visibility = View.VISIBLE
                    buttonLoginL.text = getString(R.string.next)
                    loading.visibility = View.INVISIBLE
                }
            }
        } else {
            login_error_text.text = getString(R.string.no_credentials)
            login_error_text.visibility = View.VISIBLE
            buttonLoginL.text = getString(R.string.next)
            loading.visibility = View.INVISIBLE
        }
    }

    private fun fetchCredentials(providedUID: Int): Credentials {
        var logintoken = providedUID.toString()
        var uid = 0
        var pmId = 0
        var username = ""
        var userPassword = ""
        var sessionDevice = ""
        var gender = ""

        val frame = apiget("$API_ADDRESS/user/$providedUID")

        if (!frame.isNullOrEmpty()) {
            val jsonArray = JSONArray(frame)
            val jsonObj = jsonArray.getJSONObject(0)
            //Log.d("RESPONSE_OBJECT", jsonobj.toString())
            val id = jsonObj.getString("id").toString()
            val lt = jsonObj.getString("loginToken").toString()

            if (lt == "m") {
                val frame2 = apiget("$API_ADDRESS/medic/$providedUID")
                if (!frame.isNullOrEmpty()) {
                    val jsonArray2 = JSONArray(frame2)
                    val jsonObj2 = jsonArray2.getJSONObject(0)
                    pmId = jsonObj2.getInt("idMedic")
                    username = jsonObj2.getString("firstName").toString()
                    gender = jsonObj2.getString("sex").toString()
                }
            } else {
                val frame2 = apiget("$API_ADDRESS/patient/$providedUID")
                if (!frame.isNullOrEmpty()) {
                    val jsonArray2 = JSONArray(frame2)
                    val jsonObj2 = jsonArray2.getJSONObject(0)
                    pmId = jsonObj2.getInt("idpatient")
                    username = jsonObj2.getString("firstName").toString()
                    gender = jsonObj2.getString("sex").toString()
                }
            }

            uid = id.toInt()
            userPassword = jsonObj.getString("password").toString()
            logintoken = lt
            sessionDevice = jsonObj.getString("session_active").toString()
        }

        return Credentials(uid, pmId, logintoken, username, userPassword, sessionDevice, gender)
    }

    private fun handlePassword(uidPasswd: String): Boolean {
        val encryptedpswd = EncryptionEngine.encryptsha1(passwd.editText?.text.toString())
        return encryptedpswd == uidPasswd
    }

    private fun startMode(loginToken: String, name: String, uid: Int, pmId: Int, gender: String) {
        val intent: Intent
        loading.visibility = View.INVISIBLE
        when (loginToken) {
            Constants.MODE_PATIENT -> {
                intent = Intent(context, PatientMode::class.java)
                intent.putExtra("UID", uid.toString())
                intent.putExtra("Username", name)
                intent.putExtra("PatientId", pmId.toString())
                intent.putExtra("Gender", gender)
                startActivity(intent)
                buttonLoginL.text = getString(R.string.next)
            }
            Constants.MODE_MEDIC -> {
                intent = Intent(context, MedicMode::class.java)
                intent.putExtra("UID", uid.toString())
                intent.putExtra("Username", name)
                intent.putExtra("MedicId", pmId.toString())
                intent.putExtra("Gender", gender)
                startActivity(intent)
                buttonLoginL.text = getString(R.string.next)
            }
            else -> {
                //Toast.makeText(context, R.string.wrong_credentials, Toast.LENGTH_SHORT).show()
            }
        }
    }

}