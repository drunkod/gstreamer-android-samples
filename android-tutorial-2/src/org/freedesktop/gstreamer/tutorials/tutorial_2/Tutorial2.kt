package org.freedesktop.gstreamer.tutorials.tutorial_2

import android.app.NativeActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import org.freedesktop.gstreamer.GStreamer.init

class Tutorial2 : NativeActivity() {
    private external fun nativeInit() // Initialize native code, build pipeline, etc
    private external fun nativeFinalize() // Destroy pipeline and shutdown native code
    private external fun nativePlay(text: String?) // Set pipeline to PLAYING
    private external fun nativePause() // Set pipeline to PAUSED
    private val nativeCustomData: Long = 0 // Native code will use this to keep private data:
    private var isPlayingDesired = false // Whether the user asked to go to PLAYING

    private var isLocalMedia = false // Whether this clip is stored locally or is being streamed
    private external fun nativeSetUri(uri: String?)// Set the URI of the media to play
    private var mediaUri: String? = null // URI of the clip being played
    private val  defaultMediaUri : String = "https://gstreamer.freedesktop.org/data/media/sintel_trailer-368p.ogv"

    // Called when the activity is first created.
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        // Get references to TextView and EditText
        val textViewPrompt = findViewById<TextView>(R.id.textView_prompt)
        val pipelineInput = findViewById<View>(R.id.pipeline_input) as EditText
        // Set click listener on EditText to retrieve entered URI
//        pipelineInput.setOnClickListener {
//          val enteredUri = pipelineInput.text.toString()
//
//          // Validate and process URI here
//
//          // For example:
//          if (enteredUri.isEmpty()) {
//            textViewPrompt.error = "Please enter a URI"
//          } else {
//            // URI is valid, continue processing
//              initializeGStreamer()
//          }
//        }
        // Get reference to Button
        val save = findViewById<View>(R.id.button_save) as ImageButton

        // Set click listener on Button
        save.setOnClickListener {

          // Get the text entered in the EditText
          val enteredText = pipelineInput.text.toString()

          // Validate text
          if (enteredText.isEmpty()) {
            // Show error if empty
            textViewPrompt.error = "Please enter a URI"
          } else {
            // Text is valid, continue processing

            // For example, display Toast with entered URI
//            Toast.makeText(this, enteredText, Toast.LENGTH_SHORT).show()
             initializeGStreamer()

             nativeInit()
          }
        }

        val play = findViewById<View>(R.id.button_play) as ImageButton
        play.setOnClickListener {
            isPlayingDesired = true
            nativePlay(pipelineInput.text.toString())
        }
        val pause = findViewById<View>(R.id.button_stop) as ImageButton
        pause.setOnClickListener {
            isPlayingDesired = false
            nativePause()
        }
        if (savedInstanceState != null) {
            isPlayingDesired = savedInstanceState.getBoolean("playing")
            Log.i("GStreamer", "Activity created. Saved state is playing:$isPlayingDesired")
        } else {
            isPlayingDesired = false
            Log.i("GStreamer", "Activity created. There is no saved state, playing: false")
        }

        // Start with disabled buttons, until native code is initialized
        findViewById<View>(R.id.button_play).isEnabled = false
        findViewById<View>(R.id.button_stop).isEnabled = false
//        nativeInit()

    }


    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("GStreamer", "Saving state, playing:$isPlayingDesired")
        outState.putBoolean("playing", isPlayingDesired)
    }

    override fun onDestroy() {
        nativeFinalize()
        super.onDestroy()
    }

    private fun initializeGStreamer() {
          //  init(this) делает текущий контекст Activity
          //  доступным для инициализации собственного кода и ресурсов GStreamer.
        // Initialize GStreamer and warn if it fails
        try {
            init(this)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }
    // Set the URI to play, and record whether it is a local or remote file
    private fun setMediaUri() {
          nativeSetUri(mediaUri)
          isLocalMedia = mediaUri?.startsWith("file://") ?: false
        }
    // Called from native code. This sets the content of the TextView from the UI thread.
    private fun setMessage(message: String) {
        val tv = findViewById<View>(R.id.textview_message) as TextView
        runOnUiThread { tv.text = message }
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private fun onGStreamerInitialized() {
        Log.i("GStreamer", "Gst initialized. Restoring state, playing:$isPlayingDesired")
        // Restore previous playing state
        //        setMediaUri() frizee!!!
        // Restore previous playing state
        if (isPlayingDesired) {
            val pipelineInput = findViewById<View>(R.id.pipeline_input) as EditText
            nativePlay(pipelineInput.text.toString())
        } else {
            nativePause()
        }

        // Re-enable buttons, now that GStreamer is initialized
        val activity: NativeActivity = this
        runOnUiThread {
            activity.findViewById<View>(R.id.button_play).isEnabled = true
            activity.findViewById<View>(R.id.button_stop).isEnabled = true
        }
    }

    companion object {
        @JvmStatic
        private external fun nativeClassInit(): Boolean // Initialize native class: cache Method IDs for callbacks

        init {
            System.loadLibrary("na_subclass_jni")
            System.loadLibrary("gstreamer_android")
            System.loadLibrary("tutorial-2")
            nativeClassInit()
        }
    }

        override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("onNewIntent", "Intent:${intent.dataString}")
        notifyOnNewIntent()
    }

    private external fun notifyOnNewIntent()
}