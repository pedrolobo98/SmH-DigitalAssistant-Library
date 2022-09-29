# Smart Health Digital Assistant - Library
Smart Health Digital assistant is a chatbot developed to help patients in mental health treatments (incomplete - TODO)

Min SDK: 21
jvmTarget: 1.8

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

3. Call the function from your Main Activity:
```kotlin
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    (CODE HERE - TODO)
    
  }
}
```

# How to run
abcdef
