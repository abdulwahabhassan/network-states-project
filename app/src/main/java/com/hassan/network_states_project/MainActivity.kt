package com.hassan.network_states_project

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hassan.network_states_project.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkManager: NetworkManager
    private lateinit var progressBar: ProgressBar
    private var snackbar: Snackbar? = null
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        snackbar = Snackbar.make(
            binding.root,
            "No network available, check connection",
            Snackbar.LENGTH_INDEFINITE
        )

        val _mainViewModel: MainViewModel by viewModels()
        mainViewModel = _mainViewModel


        networkManager = NetworkManager(this, lifecycle)
        lifecycle.addObserver(networkManager) //This code registers networkManager as an observer
        //after it has been initialized and passed a reference to the lifecycle of MainActivity,
        //now MainActivity recognizes networkManager as an observer hence will keep it informed about
        //any change in its lifecycle

        networkManager.networkState.observe(this, {
            if (it == NetworkAvailability.Unavailable) {
                progressBar.visibility = GONE
                snackbar?.show()
            } else {
                progressBar.visibility = VISIBLE
                snackbar?.dismiss()
            }
        })

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted {
            //The terminal operator .collect starts the cold flow's emission
            //In order to conserve system resources and keep our flow emitting only when this activity is started,
            //we have scoped it to the started state of our activity's lifecycle
            //flows can only be collected in a coroutine hence the reason we had to launch one
            //This ensure we do not block whatever thread it runs on, in this case, the main thread
            withTimeoutOrNull(21000) {
                //Runs a given suspending block of code inside a coroutine with a specified timeout
                //and returns null if this timeout was exceeded. It cancels the code running within its block
                mainViewModel.callFlow().catch {
                    //we can chain multiple terminal operations to a flow emission
                    //.catch catches any exception that is thrown while this flow emits and cancels the flow
                    //It does not however catch exception that are thrown to cancel to cancel the flow such as withTimeoutOrNull
                     value ->
                        //A snack bar to show the emitted value we are collecting
                        Snackbar.make(binding.root, "We have an Exception $value", Snackbar.LENGTH_SHORT)
                            .show()
                }.collect {
                        value ->
                    //A snack bar to show the emitted value we are collecting
                    Snackbar.make(binding.root, "Collecting flow $value", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}