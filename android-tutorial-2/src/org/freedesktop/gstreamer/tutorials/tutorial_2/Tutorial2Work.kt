package org.freedesktop.gstreamer.tutorials.tutorial_2

import android.app.NativeActivity
import android.content.Intent
import android.os.Bundle

//import org.freedesktop.gstreamer.GStreamer.init

class Tutorial2Work : NativeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        notifyOnNewIntent()
    }

    private external fun notifyOnNewIntent()

    companion object {
        init {
            System.loadLibrary("na_subclass_jni")
        }
    }
}