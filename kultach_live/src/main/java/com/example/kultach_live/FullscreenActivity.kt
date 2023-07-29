package com.example.kultach_live

import android.support.v7.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.kultach_live.databinding.ActivityFullscreenBinding

import java.util.concurrent.TimeUnit;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.Version;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private var isPlayingDesired = false // Whether the user asked to go to PLAYING

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }

            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.fullscreenContent
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(delayHideTouchListener)

//        setContentView(R.layout.activity_fullscreen)

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

//             nativeInit()
          }
        }

        val play = findViewById<View>(R.id.button_play) as ImageButton
        play.setOnClickListener {
            isPlayingDesired = true
//            nativePlay(pipelineInput.text.toString())
        }
        val pause = findViewById<View>(R.id.button_stop) as ImageButton
        pause.setOnClickListener {
            isPlayingDesired = false
//            nativePause()
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
            super.onSaveInstanceState(outState)
            Log.d("GStreamer", "Saving state, playing:$isPlayingDesired")
        outState.putBoolean("playing", isPlayingDesired)
    }

    override fun onDestroy() {
//        nativeFinalize()
        super.onDestroy()
    }

    private fun initializeGStreamer() {
          //  init(this) делает текущий контекст Activity
          //  доступным для инициализации собственного кода и ресурсов GStreamer.
        // Initialize GStreamer and warn if it fails
        try {
//            init(this)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }
    // Set the URI to play, and record whether it is a local or remote file
//    private fun setMediaUri() {
//          nativeSetUri(mediaUri)
//          isLocalMedia = mediaUri?.startsWith("file://") ?: false
//        }
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
//            nativePlay(pipelineInput.text.toString())
        } else {
//            nativePause()
        }

        // Re-enable buttons, now that GStreamer is initialized
        val activity: AppCompatActivity = this
        runOnUiThread {
            activity.findViewById<View>(R.id.button_play).isEnabled = true
            activity.findViewById<View>(R.id.button_stop).isEnabled = true
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}