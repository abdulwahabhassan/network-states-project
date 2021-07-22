package com.hassan.network_states_project

import android.content.Context
import android.net.*
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService


class NetworkManager(private val context: Context) {
    //create an instance of connectivity manager
    //Connectivity Manager tells your app about the state of connectivity in the system.
    private val connectivityManager = getSystemService(context, ConnectivityManager::class.java)

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun registerDefaultCallBack() {
        connectivityManager?.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {

            //called when a new network becomes the default
            override fun onAvailable(network: Network) {
                Log.e(TAG, "The default network is now: $network")
            }

            //called when default network loses status of being the default network
            override fun onLost(network: Network) {
                Log.e(TAG,
                    "The application no longer has a default network. The last default network was $network"
                )
            }

            //called when default network changed capabilities
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.e(TAG, "The default network changed capabilities: $networkCapabilities")
            }

            //called when default network changed linked properties
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                Log.e(TAG, "The default network changed link properties: $linkProperties")
            }
        })
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

            //called when a new network becomes the default
            override fun onAvailable(network: Network) {
                Log.e(TAG, "The default network is now: $network")
            }

            //called when default network loses status of being the default network
            override fun onLost(network: Network) {
                Log.e(TAG,
                    "The application no longer has a default network. The last default network was $network"
                )
            }

            //called when default network changed capabilities
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.e(TAG, "The default network changed capabilities: $networkCapabilities")
            }

            //called when default network changed linked properties
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                Log.e(TAG, "The default network changed link properties: $linkProperties")
            }
        })
    }


    companion object{
        const val TAG = "netWorkQuery"
    }

}