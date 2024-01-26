package com.app.test.activity

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.util.Timer
import java.util.TimerTask

class GoogleService : Service(), LocationListener {
    var isGPSEnable = false
    var isNetworkEnable = false
    var latitude = 0.0
    var longitude = 0.0
    var locationManager: LocationManager? = null
    var location: Location? = null
    private val mHandler = Handler()
    private var mTimer: Timer? = null
    var notify_interval: Long = 1000
    var intent: Intent? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mTimer = Timer()
        mTimer!!.schedule(TimerTaskToGetLocation(), 1, notify_interval)
        intent = Intent(str_receiver)
        //        fn_getlocation();
    }

    override fun onLocationChanged(location: Location) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    private fun fn_getlocation() {
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        isGPSEnable = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnable = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnable && !isNetworkEnable) {
        } else {
            if (isNetworkEnable) {
                location = null
                locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0f,
                    this
                )
                if (locationManager != null) {
                    location =
                        locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location != null) {
                        Log.e("latitude", location!!.latitude.toString() + "")
                        Log.e("longitude", location!!.longitude.toString() + "")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        fn_update(location!!)
                    }
                }
            }
            if (isGPSEnable) {
                location = null
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0f,
                    this
                )
                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        Log.e("latitude", location!!.latitude.toString() + "")
                        Log.e("longitude", location!!.longitude.toString() + "")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        fn_update(location!!)
                    }
                }
            }
        }
    }

    private inner class TimerTaskToGetLocation : TimerTask() {
        override fun run() {
            mHandler.post { fn_getlocation() }
        }
    }

    private fun fn_update(location: Location) {
        intent!!.putExtra("latutide", location.latitude.toString() + "")
        intent!!.putExtra("longitude", location.longitude.toString() + "")
        sendBroadcast(intent)
    }

    companion object {
        @JvmField
        var str_receiver = "receiver"
    }
}