package com.example.speechtotextpoc

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.speechtotextpoc.databinding.ActivityMainBinding
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.speechRec.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                checkPermission()
            } else {
                askSpeechInput()
            }
        }

    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(android.Manifest.permission.RECORD_AUDIO), 101
            )
        }
    }

    private val speechRecognitionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                Log.d("Speech Data", result.toString())
                if (data != null && result != null) {
                    try {
                        runOnUiThread {
                            activityMainBinding.textView.setText((result)[0].toString())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    activityMainBinding.textView.setText("No Data")
                }
            }
        }
    // Handle the speech recognition results here


    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech rec not available", Toast.LENGTH_LONG).show()
        } else {
            Log.d("Speech to text", SpeechRecognizer.isRecognitionAvailable(this).toString())
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!")
            speechRecognitionLauncher.launch(intent)
        }
    }
}