package com.hassan.network_states_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class MainViewModel : ViewModel() {

    //creates a cold flow that is only called when a terminal operator such as collect is applied to
    //its resulting flow
    fun callFlow() = flow {
        //whatever code is written within the flow builder body is suspendable
        //that's why we can call delay which is a suspend function
            var counter = 0
        do{
            //emit emits a given value as a flow which can be collected
            emit(counter)
            if (counter == 3) {
                //we mimic the case of a thrown exception to see our flow catch it
                throw IOException("ERROR")
            }
            counter++
            //Delays coroutine for a for 3 sec without blocking the thread and resumes it after
            delay(3000)
        } while (
            counter < 20

        )
    }

    fun getSequence() = sequenceOf("Alan", "Jack", "Manuel", "Sam")

}