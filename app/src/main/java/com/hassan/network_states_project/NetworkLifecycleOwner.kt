package com.hassan.network_states_project

import androidx.lifecycle.*

class NetworkLifecycleOwner : LifecycleOwner {
    //this is a custom lifecycleOwner we made for improved synchronization

    //LifecycleRegistry is an implementation of Lifecycle that can handle multiple observers.
    private val lifecycleRegistry = LifecycleRegistry(this)

    //We override this method whenever we implement LifecycleOwner
    override fun getLifecycle() = lifecycleRegistry

    //on connection lost, this event will be dispatched
    fun connectionLost() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
    //on connection available, this event will be dispatched
    fun connectionAvailable() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }
    //this will be used to add new observers
    fun addObserver(lifecycleObserver: LifecycleObserver) {
        lifecycleRegistry.addObserver(lifecycleObserver)
    }

}