package com.hassan.network_states_project

//Lifecycle aware components perform actions in response to change in the lifecycle of another components,
//such as an Activity or a Fragment.

//By using lifecycle-aware components, we can move the code of dependent components out of the
//lifecycle methods of Activities and Fragments for example into the components themselves and simply
//make the dependent component aware of the lifecycle of any activity or fragment in particular that we want

//The androidx.lifecycle package provides classes and interfaces that help make components life-cycle aware

//Lifecycle is a class that holds the information about the lifecycle state of a component
//(like an activity or a fragment) and allows other objects to observe this state.

//Lifecycle uses two main enumerations to track the lifecycle status for its associated component: State and Event

//lifecycle events that are dispatched from the framework and the Lifecycle class.
//These events map to the callback events in activities and fragments, these are: ON_CREATE, ON_RESUME,
//ON_PAUSE, ON_STOP, ON_START, ON_DESTROYED

//lifecycle state refers to the current state of a component's lifecycle, this could be any of:
//Initialized(initial state), Destroyed(dead state), Created, Started, Resumed

//Think of the states as nodes of a graph and events as the edges between these nodes.

//A class can monitor a component's lifecycle status by adding annotations to its methods. Then,
//you can add an observer by calling the addObserver() method of the Lifecycle class and passing an instance of your observer

//In our case, our network manager, which is responsible for monitoring the state of network on the device that
//open our app is a lifecycle observer that will respond accordingly to the corresponding lifecycle events of
//which ever component(Activity or Fragment) we associate it with. it becomes an observer by extending the class
//LifecycleObserver

//To associate a fragment or an activity with a lifecycle observer, we simple get its lifecycle and call addObserver, passing in
//our observer of choice e.g <fragment or activity>.getLifecycle().addObserver(MyObserver())

//A component must be a LifecycleOwner or implement the LifecycleOwner interface for it to manage a
//LifeCycleObserver
//The LifecycleOwner is a single method interface that denotes a component or class has a lifecycle.
//Any custom application class can implement the LifecycleOwner interface.
//This interface abstracts the ownership of a Lifecycle from individual classes, such as Fragment and AppCompatActivity, and allows writing components that work with them.
//It has a single method getLifecycle() which is used to retrieve its lifecycle and must be implemented by a class
//that extends its interface, LifecycleOwner
//A lifecycleOwner provides a lifecycle which a lifecycleObserver can register to watch

//ProcessLifecycleOwner is the interface that is used in the case of managing a whole application process
//This class provides a lifecycle for the entire app process. It dispatches ON_CREATE only once,
//when the app starts for the first time. It won’t dispatch ON_DESTROY at all.
//ProcessLifecycleOwner dispatches ON_START and ON_RESUME when the first activity in the app goes through these states.
//Finally, ProcessLifecycleOwner dispatches ON_PAUSE and ON_STOP events after the last visible activity of the app goes through them.
//It’s important to know that these last two events will happen after a certain delay.
//There’s a delay because ProcessLifecycleOwner needs to be sure of the reason for these changes.
//It only needs to dispatch these events if they happen because the app went to the background and not due to configuration changes.
//We can use ProcessLifecycleOwner to track whenever the app comes to the foreground or goes to the background.
//As this happens when the lifecycle owner sends the ON_START and ON_STOP events respectively.
//Any class or component can track the lifecycle changes of the application by making such class or component extend
//lifecycleObserver. However to register the component or class with the application(lifecycleOwner), we do so
//in a slightly different like so: ProcessLifecycleOwner.get().lifecycle.addObserver(observerComponent)
//This method is called in the onCreate of the application

//A component A that wants to observe the lifecycle of another component B will be instantiated in the onCreate method
//of the Component B and passed the lifecycle of the component B as a constructor parameter. This makes the newly
//instantiated component A aware of any lifecycle events happening in component B.It can then react appropriately
//during whichever event callback it wants to.

//We can query the current state of a lifecycle e.g lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
//and perform operation based on those queries. This can help to guarantee that certain operations are performed
//safely when the lifecycle is in a given state

//LifecycleRegistry is an implementation of Lifecycle that can handle multiple observers.
//It is used by Fragments and Support Library Activities. You can also directly use it if you have a custom LifecycleOwner.
//If you have a custom class that you would like to make a LifecycleOwner,
//you can use the LifecycleRegistry class, but you need to forward events into that class

//The lifecycle of fragments can be considerably longer than the lifecycle of the view they contain.
//If an observer interacts with the user interface (views) in a fragment, this can cause a problem because
//the observer can modify a view before it’s initialized yet or after it’s destroyed.
//So sometimes we use viewLifecycleOwner within Fragments.
//You can start using this lifecycle owner during onCreateView() and before onDestroyView().
//Once the view lifecycle gets destroyed, it won’t dispatch any more events.

//We cam create a custom lifecycleOwner that other components can observe. We simply extend the lifecycleOwner
//on our custom class or component, make an instance of LifecycleRegistry within this custom class, passing in
//the class itself as a provider to the constructor of the LifecycleRegistry, then overriding getLifecycle
//so that it returns the LifecycleRegistry instance. After this, we make use of handleLifecycleEvent to
//set the current state of the lifecycle and notify observers when a lifecycle events has occurs.