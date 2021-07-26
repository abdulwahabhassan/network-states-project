package com.hassan.network_states_project

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ApplicationObserver(private val context: Context) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun toastBackground() {
        Log.i("AppObserver", "Advancing to background")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun toastForeground() {
        Log.i("AppObserver", "Advancing to foreground")
    }
}