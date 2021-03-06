package com.hassan.network_states_project

import android.content.Context
import android.net.*
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//Note: Some of the explanations in this note might be somehow confusing due to slight modifications
//in the code. I may have abstracted away some classes and methods for better organization of the code
//So you may not find the relevant code to a note immediately below it **

class NetworkManager(context: Context, private val lifecycle: Lifecycle) : LifecycleObserver {
    //create an instance of connectivity manager
    //Connectivity Manager tells your app about the state of connectivity in the system.
    private val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
    private lateinit var defaultNetworkCallBack : ConnectivityManager.NetworkCallback
    val job =  Job()

    //This life data updates when our network states changes and is being observed in main activity
    val networkState = MutableLiveData<NetworkAvailability>(NetworkAvailability.Unavailable)


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initDefaultNetworkCallback () {
        defaultNetworkCallBack = object : ConnectivityManager.NetworkCallback() {

            //called when a new network becomes the default
            override fun onAvailable(network: Network) {
                Log.e(TAG, "The default network is now: $network")

            }

            //called when default network loses status of being the default network
            override fun onLost(network: Network) {
                Log.e(TAG,
                    "The application no longer has a default network. The last default network was $network")
                CoroutineScope(Dispatchers.Main + job).launch {
                    networkState.value = NetworkAvailability.Unavailable
                }
            }

            //called when default network changed capabilities
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.e(TAG, "The default network changed capabilities: $networkCapabilities")
                 if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                     //This launches a coroutine that sets the value of our mutable live data to Available
                     //once we have a default network with NET_CAPABILITY_VALIDATED
                     //We use a coroutine to set the value on the main thread since we cannot
                     //set value on a background thread
                     //The job object is used to hold a ref to the coroutine for cancelling later
                     CoroutineScope(Dispatchers.Main + job).launch {
                         networkState.value = NetworkAvailability.Available
                     }
                } else {
                     CoroutineScope(Dispatchers.Main + job).launch {
                         networkState.value = NetworkAvailability.Unavailable
                     }
                }

            }

            //called when default network changed linked properties
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                Log.e(TAG, "The default network changed link properties: $linkProperties")
            }
        }
    }

    //ensure to have declared ACCESS_NETWORK_STATE in manifest file
    //get reference to current default network
    private val currentNetwork = connectivityManager?.activeNetwork

    //with a reference to network, we can query information about it
    //This kind of queries though only query instantaneous state and are not really useful for most
    //applications save for debugging purpose

    //getNetworkCapabilities returns a NetworkCapabilities object which contains information
    //about the properties of a network
    val networkCapabilities = connectivityManager?.getNetworkCapabilities(currentNetwork)

    //The NetworkCapabilities object encapsulates information about the network transports and their
    //capabilities.
    //A transport is an abstraction of a physical medium over which a network operates.
    //Common examples of transports are Ethernet, Wi-Fi, and cellular, but it can also include VPN
    //or Peer-to-Peer Wi-Fi.
    //In Android, a network can have multiple transports at the same time e.g a VPN operating over
    //both Wi-Fi and cellular networks
    //An app can check if a network has a particular transport by calling NetworkCapabilities.hasTransport(int)
    //and passing in any of TRANSPORT_CELLULAR, TRANSPORT_WIFI, TRANSPORT_VPN etc.

    //An app can check for a network's capabilities by calling NetworkCapabilities.hasCapability(int) method
    //and passing in any of these constants: NET_CAPABILITY_INTERNET, NET_CAPABILITY_NOT_METERED,
    //NET_CAPABILITY_NOT_VPN, NET_CAPABILITY_VALIDATED, NET_CAPABILITY_CAPTIVE_PORTAL as parameters.
    //Other capabilities exist beyond these for specialized apps. A network may have any of these
    //capabilities at any time

    //Since we know that a networks state might change at any time, we usually register a
    //NetworkCallback to monitor a networks states
    //To find out about network events, use the NetworkCallback class together with
    //ConnectivityManager.registerDefaultNetworkCallback(NetworkCallback) and
    //ConnectivityManager.registerNetworkCallback(NetworkCallback)

    //All android app have a default network which is assigned by the system. The system decided which
    //network should be the default network based on preference for un-metered to metered and faster to slower
    //networks
    //The default network which an app chooses can change at anytime during the lifetime of an app such
    //as switching from a slower cellular network to a nearby faster wifi network.
    //To know when the default  network changes, register a default network call back like so,

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun registerDefaultCallBack() {
        //we query the current state of the lifecycle particularly for the ON_START event
        //to ensure that the lifecycle of the component we are observing is in a good state before
        //our code runs. This is more useful for codes that update the UI
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        connectivityManager?.registerDefaultNetworkCallback(defaultNetworkCallBack)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unregisterDefaultNetworkCallback() {
        connectivityManager?.unregisterNetworkCallback(defaultNetworkCallBack)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelJob() {
        job.cancel()
    }

    //callbacks should be unregistered when no longer in use by calling
    //ConnectivityManager.unregisterNetworkCallback(NetworkCallback).
    //Activity's onPause() is a good place to do this, especially if the callback is registered in onResume()

    //Sometimes we may want to build request for networks matching specific needs other than the default
    //network which the system provides. In that case we call
    //ConnectivityManager.registerNetworkCallback(NetworkRequest, NetworkCallback) instead of
    //ConnectivityManager.registerDefaultNetworkCallback(NetworkCallback), like so:

    fun buildRequestAndRegisterNetworkCallBack() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        //We pass in the request which specifies a network that is not metered and has internet set up
        //We also pass in a callback represented by an anonymous object
        connectivityManager?.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {

            //called when a new network is available
            override fun onAvailable(network: Network) {
                Log.e(TAG, "The new network is now: $network")
            }

            //called when network is lost
            override fun onLost(network: Network) {
                Log.e(TAG, "Network lost. The lost network was $network")
            }

            //called when network changed capabilities
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.e(TAG, "The network changed capabilities: $networkCapabilities")
            }

            //called when network changed linked properties
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                Log.e(TAG, "The network changed link properties: $linkProperties")
            }
        })
    }


    companion object{
        const val TAG = "NETWORK"
    }

}

sealed class NetworkAvailability {
    object Unavailable : NetworkAvailability()
    object Available : NetworkAvailability()
}