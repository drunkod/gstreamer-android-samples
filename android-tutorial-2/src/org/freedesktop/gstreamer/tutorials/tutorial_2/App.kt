// Copyright (c) 2020 Tailscale Inc & AUTHORS All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
//package org.freedesktop.gstreamer.tutorials.tutorial_2
package org.freedesktop.gstreamer.tutorials.tutorial_2

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log

import org.freedesktop.gstreamer.GStreamer.init
import org.gioui.Gio

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Load and initialize the Go library.
        initializeGStreamer()
        Gio.init(this)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(Intent(this, MessageService::class.java))
//        } else {
//            startService(Intent(this, MessageService::class.java))
//        }
    }

    private fun initializeGStreamer() {
          //  init(this) делает текущий контекст Activity
          //  доступным для инициализации собственного кода и ресурсов GStreamer.
        // Initialize GStreamer and warn if it fails
        try {
            init(this)
        } catch (e: Exception) {
//            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()

        Log.i("GStreamer", "Gst Exception. Error:${e.toString()}")

            return
        }
    }
    companion object {
        private val mainHandler = Handler(Looper.getMainLooper())

//                @JvmStatic
//        private external fun nativeClassInit(): Boolean // Initialize native class: cache Method IDs for callbacks

        init {
            System.loadLibrary("gstreamer_android")
            System.loadLibrary("tutorial-2")
//            nativeClassInit()
        }
    }
}
