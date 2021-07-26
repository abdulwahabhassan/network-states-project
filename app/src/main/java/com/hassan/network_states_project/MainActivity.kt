package com.hassan.network_states_project

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.hassan.network_states_project.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkManager: NetworkManager
    private lateinit var progressBar: ProgressBar
    private var snackbar: Snackbar? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkLifecycleOwner: NetworkLifecycleOwner
    private var job: Job? = null

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

        networkLifecycleOwner = NetworkLifecycleOwner()
        //we made MainActivity a lifecycle observer so that it can observe NetworkLifecycleOwner
        //and react to it based on its lifecycle

        networkManager = NetworkManager(this, lifecycle)
        lifecycle.addObserver(networkManager) //This code registers networkManager as an observer
        //after it has been initialized and passed a reference to the lifecycle of MainActivity,
        //now MainActivity recognizes networkManager as an observer hence will keep it informed about
        //any change in its lifecycle

        networkManager.networkState.observe(this, Observer {
            if (it == NetworkAvailability.Unavailable) {
                networkLifecycleOwner.connectionLost()
                progressBar.visibility = GONE
                snackbar?.show()
        } else {
                networkLifecycleOwner.connectionAvailable()
                progressBar.visibility = VISIBLE
                snackbar?.dismiss()

                job?.cancel() //This cancels any previous coroutine before starting a new one
                //job holds a reference to our coroutine

               job = networkLifecycleOwner.lifecycleScope.launchWhenStarted {
                    //In order to conserve system resources and keep our flow emitting only when this activity is started,
                    //we have scoped it to the started state of our NetworkLifecycleOwner's lifecycle. We could also scope this
                    //to the activity's lifecycle if we want
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
                        }.collect { //The terminal operator .collect starts the cold flow's emission
                                value ->
                            //A snack bar to show the emitted value we are collecting
                            Snackbar.make(binding.root, "Collecting flow $value", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }

                    //.asFlow is capable of converting collections such as sequence or list to cold flows that can be collected
                    //we can add intermediate terminal operators such as .map
                    //terminal operators of flow can contain suspending functions unlike sequences or lists
                    mainViewModel.getSequence().asFlow()
                        .map{ "$it is a flow" }
                        .transform{ if (it == "Manuel is a flow") {
                            //.transform allows to apply a transformation to each event in the flow
                            //and emit those transformed values.
                            //It is a general transformation that imitates simple transformations like map and filter
                            emit("Manuel is the coolest flow")
                        } else {
                            emit(it)
                        }
                        }
                        .collect {
                            delay(2000)
                            Snackbar.make(binding.root, "Collecting $it in sequence as flow", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                }

            }
        })

    }

}