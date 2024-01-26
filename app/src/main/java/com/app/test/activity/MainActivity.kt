package com.app.test.activity

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.test.R
import com.app.test.activity.GoogleService
import java.io.IOException
import java.util.Locale

class MainActivity : Activity() {
    var btn_start: Button? = null
    var boolean_permission = false
    var tv_latitude: TextView? = null
    var tv_longitude: TextView? = null
    var tv_address: TextView? = null
    var tv_area: TextView? = null
    var tv_locality: TextView? = null
    var mPref: SharedPreferences? = null
    var medit: SharedPreferences.Editor? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    var geocoder: Geocoder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_start = findViewById<View>(R.id.btn_start) as Button
        tv_address = findViewById<View>(R.id.tv_address) as TextView
        tv_latitude = findViewById<View>(R.id.tv_latitude) as TextView
        tv_longitude = findViewById<View>(R.id.tv_longitude) as TextView
        tv_area = findViewById<View>(R.id.tv_area) as TextView
        tv_locality = findViewById<View>(R.id.tv_locality) as TextView
        geocoder = Geocoder(this, Locale.getDefault())
        mPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        medit = mPref.edit()
        btn_start!!.setOnClickListener {
            if (boolean_permission) {
                if (mPref.getString("service", "")!!.matches("".toRegex())) {
                    medit.putString("service", "service").commit()
                    val intent = Intent(applicationContext, GoogleService::class.java)
                    startService(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Service is already running",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Please enable the gps", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        fn_permission()
    }

    private fun fn_permission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_PERMISSIONS
                )
            }
        } else {
            boolean_permission = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please allow the permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            latitude = java.lang.Double.valueOf(intent.getStringExtra("latutide"))
            longitude = java.lang.Double.valueOf(intent.getStringExtra("longitude"))
            var addresses: List<Address>? = null
            try {
                addresses = geocoder!!.getFromLocation(latitude, longitude, 1)
                val cityName = addresses!![0].getAddressLine(0)
                val stateName = addresses[0].getAddressLine(1)
                val countryName = addresses[0].getAddressLine(2)
                tv_area!!.text = addresses[0].adminArea
                tv_locality!!.text = stateName
                tv_address!!.text = countryName
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            tv_latitude!!.text = latitude.toString() + ""
            tv_longitude!!.text = longitude.toString() + ""
            tv_address!!.text
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, IntentFilter(GoogleService.str_receiver))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 100
    }
}