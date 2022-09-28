package com.example.digitalassistantapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalassistantapp.R
import com.example.digitalassistantapp.adapters.ChatAdapter
import com.example.digitalassistantapp.chatResources.BotResponse
import com.example.digitalassistantapp.chatResources.MessageSender
import com.example.digitalassistantapp.chatResources.UserMessage
import com.example.digitalassistantapp.models.MessageViewModel
import com.example.digitalassistantapp.settings.SharedPreference
import com.example.digitalassistantapp.utils.Constants
import com.example.digitalassistantapp.utils.Utility
import com.example.digitalassistantapp.utils.Utility.hideKeyboard
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_chat.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ChatFragment : Fragment() {
    private lateinit var retrofit: Retrofit
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private lateinit var recognitionView: RecognitionProgressView
    private var usedVoice = false
    private val mAdapter = ChatAdapter(ArrayList())
    private val userMessage = UserMessage()
    private val heights = intArrayOf(60, 76, 58, 80, 55)
    private var ttsOn = false
    private var autoMic = false
    var busy = false
    var clicked = false

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.getDefault()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        ttsOn = SharedPreference.getTalkBack(requireContext())
        autoMic = SharedPreference.getAutoMic(requireContext())

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var textTrigger = false
        val btnBack = view.findViewById<ImageButton>(R.id.back_button)
        val senderId = arguments?.getString("patientID")
        val taskAction = arguments?.getBoolean("taskSelected")
        val taskName = arguments?.getString("taskName")
        val name = arguments?.getString("name")
        val colors = intArrayOf(ContextCompat.getColor(view.context, R.color.medical_green))

        toolbar.inflateMenu(R.menu.overflow_chat_settings)
        toolbar.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.chat_settings -> {
                    preferences(requireContext())
                }
            }
            true
        }

        btnBack.setOnClickListener {
            if (!busy){
                parentFragmentManager.popBackStackImmediate()
            }
        }

        setupChatView()
        connectChat(Constants.RASA_ADDRESS)

        if (taskAction == true) {
            if (taskName != null){
                sendMessage(taskName, senderId!!)
            }
        } else {
            mAdapter.add(MessageViewModel("Olá, $name como te sentes hoje?", "BOT"))
            if (ttsOn){
                textToSpeechEngine.speak("Olá, $name como te sentes hoje?", TextToSpeech.QUEUE_FLUSH, null, senderId)
            }
        }

        recognitionView = view.findViewById(R.id.recognition_view)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognitionView.setSpeechRecognizer(speechRecognizer)
        recognitionView.setBarMaxHeightsInDp(heights)
        recognitionView.setColors(colors)
        recognitionView.play()

        recognition_view.setRecognitionListener(object : RecognitionListenerAdapter() {
            override fun onReadyForSpeech(bundle: Bundle) {
                recognitionView.visibility = View.VISIBLE
                recognitionView.play()
                floatingSendButton.setImageResource(R.drawable.ic_baseline_mic_24)
            }
            override fun onBeginningOfSpeech() {
                Log.d("SPEECH", "Listening")
                busy = true
            }
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {
                //Log.d("SPEECH", "End of speech")
                floatingSendButton.setImageResource(R.drawable.ic_baseline_mic_none_24)
                usedVoice = true
                busy = false
            }
            override fun onError(i: Int) {
                //Log.d("SPEECH", "Speech error")
                recognitionView.stop()
                recognitionView.play()
                busy = false
            }
            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("SPEECH", data!![0])
                if (senderId != null) {
                    sendMessage(data[0], senderId)
                    recognitionView.visibility = View.GONE
                }
            }
            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        messageInputChat.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                if (messageInputChat.editText?.text.isNullOrEmpty() || messageInputChat.editText?.text.toString() == ""){
                    floatingSendButton.setImageResource(R.drawable.ic_baseline_mic_none_24) //Microphone Icon
                    textTrigger = false
                } else {
                    floatingSendButton.setImageResource(R.drawable.ic_baseline_send_24) //Send Icon
                    textTrigger = true
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        floatingSendButton.setOnClickListener { view ->
            Utility.preventOverclick(view)
            if (textTrigger) {
                //Text Handler
                if (senderId != null) {
                    sendMessage(messageInputChat.editText?.text.toString(), senderId)
                    usedVoice = false
                }
                messageInputChat.editText?.text?.clear()
                hideKeyboard()
            } else {
                //Voice Handler
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission()
                } else {
                    activateSpeechRec()
                }
            }
        }
    }

    fun sendMessage(msg: String, senderId: String){
        busy = true
        if (msg.trim().isNotEmpty()){
            //Log.d("MSG", msg)
            mAdapter.add(MessageViewModel(msg, senderId))
            chat_recyclerview.scrollToPosition(mAdapter.itemCount - 1)
            userMessage.UserMessage(senderId, msg)
        }
        val messageSender = retrofit.create(MessageSender::class.java)
        val response = messageSender.sendMessage(userMessage)
        bot_writing.visibility = View.VISIBLE
        response.enqueue(object : retrofit2.Callback<List<BotResponse>?> {
            override fun onResponse(call: retrofit2.Call<List<BotResponse>?>, response: retrofit2.Response<List<BotResponse>?>) {
                bot_writing.visibility = View.GONE
                if (response.body().isNullOrEmpty()){
                    mAdapter.add(MessageViewModel("Não percebi, podes repetir?", "BOT"))
                    if (ttsOn){
                        textToSpeechEngine.speak("Não percebi, podes repetir?", TextToSpeech.QUEUE_FLUSH, null, senderId)
                    }
                    chat_recyclerview.scrollToPosition(mAdapter.itemCount - 1)
                } else {
                    for (i in response.body()!!.indices - 1) {
                        mAdapter.add(MessageViewModel(response.body()!![i].text, "BOT"))
                        chat_recyclerview.scrollToPosition(mAdapter.itemCount - 1)
                        if (ttsOn) {
                            textToSpeechEngine.speak(response.body()!![i].text, TextToSpeech.QUEUE_FLUSH, null, senderId)
                        }
                        if (usedVoice && autoMic) {
                            recognitionView.stop()
                            recognitionView.play()
                        }
                        if (response.body()!![i].buttons != null) {
                            val buttonRecyclerView = ButtonRecyclerView(view!!.context, response.body()!![i].buttons, senderId)
                            val layoutManager = LinearLayoutManager(view!!.context)
                            layoutManager.orientation = LinearLayoutManager.VERTICAL
                            button_list.layoutManager = layoutManager
                            button_list.adapter = buttonRecyclerView
                        } else {
                            button_list.adapter = null
                        }
                    }
                }
                busy = false
                clicked = false
            }
            override fun onFailure(call: retrofit2.Call<List<BotResponse>?>, t: Throwable) {
                busy = false
                clicked = false
                t.message?.let { Log.e("CHAT_FAILURE", it) }
                Toast.makeText(context, "Falha de comunicação: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun activateSpeechRec(){
        recognitionView.play()
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun connectChat(address: String){
        retrofit = Retrofit.Builder()
            .baseUrl(address)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun setupChatView() {
        with(chat_recyclerview){
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    inner class ButtonRecyclerView(var context: Context, private var buttons: List<BotResponse.Buttons>, private var senderId: String):
            RecyclerView.Adapter<ButtonRecyclerView.ButtonViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonRecyclerView.ButtonViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.view_buttons, parent, false)
            return ButtonViewHolder(view)
        }

        override fun onBindViewHolder(holder: ButtonRecyclerView.ButtonViewHolder, position: Int) {
            val payloadButton = buttons[position]
            holder.button.text = payloadButton.title
            holder.button.setOnClickListener { view ->
                Utility.preventOverclick(view)
                if (!clicked){
                    sendMessage(payloadButton.payload, senderId)
                    clicked = true
                }
            }
        }

        override fun getItemCount(): Int {
            buttons.isEmpty()
            return buttons.size
        }

        inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val button: MaterialButton = view.findViewById(R.id.respond_button)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO),
                Constants.RECORD_AUDIO_REQUEST_CODE)
        }
    }

    private fun preferences(context: Context){
        val items = arrayOf("Voz do Chatbot", "Microfone Automático")
        val checkedItems = booleanArrayOf(ttsOn, autoMic)
        MaterialAlertDialogBuilder(context)
            .setTitle("Preferências")
            .setNegativeButton("Cancelar"){_, _ ->

            }
            .setPositiveButton("Guardar"){_, _ ->
                SharedPreference.setTalkBack(context, ttsOn)
                SharedPreference.setAutoMic(context, autoMic)
            }
            .setMultiChoiceItems(items, checkedItems) {_, which, checked ->
                checkedItems[which] = checked

                if (items[which].contentEquals("Voz do Chatbot")) {
                    ttsOn = checkedItems[which]
                } else if (items[which].contentEquals("Microfone Automático")) {
                    autoMic = checkedItems[which]
                }
            }
            .show()
    }

    override fun onPause() {
        super.onPause()
        recognitionView.visibility = View.GONE
        speechRecognizer.stopListening()
        textToSpeechEngine.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        recognitionView.visibility = View.GONE
        speechRecognizer.destroy()
        textToSpeechEngine.shutdown()
    }

    override fun onResume() {
        super.onResume()
        ttsOn = SharedPreference.getTalkBack(requireContext())
        autoMic = SharedPreference.getAutoMic(requireContext())
    }
}
