package com.elhady.superstore

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.schedulers.Schedulers

// manage the application lifecycle
class StoreApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        listenToNetworkConnectivity()
    }

    @SuppressLint("CheckResult")
    fun listenToNetworkConnectivity() {
        ReactiveNetwork.observeInternetConnectivity().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io()).subscribe { isConnected: Boolean ->
                Log.d(TAG, "Connected to internet: $isConnected")
                FirebaseCrashlytics.getInstance().setCustomKey("connected_to_internet", isConnected)
            }
    }

    companion object {
        private const val TAG = "StoreApplication"
    }
}