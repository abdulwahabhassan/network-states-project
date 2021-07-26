package com.hassan.network_states_project

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class NSPApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val applicationObserver = ApplicationObserver(applicationContext)
        //adds an observer to the application lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationObserver)
    }
}