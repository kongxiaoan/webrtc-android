package com.webrtc.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.webrtc.android.databinding.ActivityMainBinding
import com.webrtc.android.websocket.WebsocketClient

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebsocketClient()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'android' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'android' library on application startup.
        init {
            System.loadLibrary("webrtc-android")
        }
    }
}
