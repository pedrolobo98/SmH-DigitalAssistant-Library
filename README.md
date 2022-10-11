# Smart Health Digital Assistant - Library
Smart Health Digital assistant is a portuguese language chatbot developed to help patients in mental health treatments. It helps the patients doing treatments at home by encouraging them to do their daily tasks and to maintain a constant companion, shortening the distance between the medic and the patient, as it also allows the medic to do a daily monitoring of the patient.

In order to work properly, this digital assistant must be connected to a specific MySQL database API and a [Rasa chatbot system](https://rasa.com/).

This app was developed with Kotlin language with the minimum SDK: 21.

# How to install
To install the library you need to create a new Android project and follow the next steps:

1. Add the following library dependencies in the gradle file:
```kotlin
dependencies {
  implementation 'io.github.pedrolobo98:SmH-DigitalAssistant-Library:1.0.0'
}
```

2. Add the permissions in the AndroidManifest.xml file:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools"
  package="com.example.digitalassistantapp">
  
  <uses-permission android:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.RECORD_AUDIO"/>
  <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
  <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
  
  <application
  ...>
  </application>
  
</manifest>
```

3. Create the following constants for the API Adresses (chatbot server and database):
```kotlin
var RASA_ADDRESS = "https://example.rasa-chabot-server.com/webhooks/rest/"
var API_ADDRESS = "https://example.database-api.com"
```

4. Call the function from your Activity or Fragment:

  The following code represents an example function to load the two available modes of the app, the patient mode and the medic mode.
```kotlin
private fun startMode(loginToken: String, name: String, uid: Int, pmId: Int, gender: String) {
  val intent: Intent
  loading.visibility = View.INVISIBLE
  when (loginToken) {
    Constants.MODE_PATIENT -> {
      intent = Intent(context, PatientActivity::class.java)
      intent.putExtra("UID", uid.toString())
      intent.putExtra("Username", name)
      intent.putExtra("PatientId", pmId.toString())
      intent.putExtra("Gender", gender)
      startActivity(intent)
    }
    Constants.MODE_MEDIC -> {
      intent = Intent(context, MedicActivity::class.java)
      intent.putExtra("UID", uid.toString())
      intent.putExtra("Username", name)
      intent.putExtra("MedicId", pmId.toString())
      intent.putExtra("Gender", gender)
      startActivity(intent)
    }
  }
}
```

**NOTE:** You must create the rasa chatbot server and the database or have access to a third party one, as this is only the Android application part. Also keep in mind that the app was developed in portuguese language only.

# Example Run

**Patient mode**

In the patient mode you can chat with the chatbot, do your daily tasks, check your medical calendar appointments and check your health register (WIP).

<p float="left">
  <img src=https://user-images.githubusercontent.com/34798263/193275214-d5a602e8-d48a-4b9c-a012-fa6b0ce69573.png width=193/>
  <img src=https://user-images.githubusercontent.com/34798263/193275290-7fd0e27f-54f7-482d-a1b2-1e8a989208e1.png width=193/>
  <img src=https://user-images.githubusercontent.com/34798263/193275312-8049cc73-7200-40b4-96c1-4006b44f2904.png width=193/>
</p>

**Medic mode**

The medic mode you can check all your patients, monitor their evolution in the daily tasks and customize their treatment plan.

<p float="left">
  <img src=https://user-images.githubusercontent.com/34798263/193275446-659b111c-936c-47a5-b73f-3b7407e1e2f3.png width=200/>
  <img src=https://user-images.githubusercontent.com/34798263/193275475-c3449f9d-84af-4443-8655-3bd66ebc2105.png width=383/>
</p>
