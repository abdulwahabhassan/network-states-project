package com.hassan.network_states_project

import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hassan.network_states_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkManager: NetworkManager
    private lateinit var progressBar: ProgressBar
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkManager = NetworkManager(this, lifecycle)
        lifecycle.addObserver(networkManager) //This code registers networkManager as an observer
        //after it has been initialized and passed a reference to the lifecycle of MainActivity,
        //now MainActivity recognizes networkManager as an observer hence will keep it informed about
        //any change in its lifecycle
        progressBar = binding.progressBar
        snackbar = Snackbar.make(binding.root, "No network available, turn on network and re-launch app", Snackbar.LENGTH_INDEFINITE)
    }

    override fun onStart() {
        super.onStart()
        if (networkManager.networkCapabilities?.hasCapability(NET_CAPABILITY_VALIDATED) == true) {
            progressBar.visibility = VISIBLE
            snackbar?.dismiss()
        } else {
            progressBar.visibility = GONE
            snackbar?.show()
        }

    }

}