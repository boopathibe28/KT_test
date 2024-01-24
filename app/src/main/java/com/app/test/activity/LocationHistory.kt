package com.app.test.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.test.R
import com.app.test.adapter.AddressListAdapter
import com.app.test.app_interfaces.ListOnclick
import com.app.test.databinding.ActivityLocationHistoryBinding
import com.app.test.realm_db.RealmLibrary
import com.app.test.utils.CommonFunctions
import com.app.test.utils.GPSTracker
import com.app.test.utils.LocationOnOff_Similar_To_Google_Maps
import com.app.test.utils.MyApplication
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_location_history.rlyoutLocationHistory
import kotlinx.android.synthetic.main.activity_location_history.rlyoutMapView
import kotlinx.android.synthetic.main.activity_location_history.rvList
import kotlinx.android.synthetic.main.activity_location_history.tvBack
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationHistory : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var gpsTracker: GPSTracker? = null
    var current_address = ""
    var binding: ActivityLocationHistoryBinding? = null
    var tempsavedInstanceState: Bundle? = null
    private var googleMap: GoogleMap? = null
    private var markerLatitude = 0.0
    private var markerLongitude = 0.0
    private var str_address = ""
    private var str_time = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempsavedInstanceState = savedInstanceState
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_history)
        rlyoutLocationHistory.visibility = View.VISIBLE
        rlyoutMapView.visibility = View.GONE
        val intent = Intent(this@LocationHistory, LocationOnOff_Similar_To_Google_Maps::class.java)
        startActivity(intent)
        gpsTracker = GPSTracker(this@LocationHistory)
        mGoogleApiClient =
            GoogleApiClient.Builder(this) // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this) //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build()

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval((10 * 1000).toLong()) // 10 seconds, in milliseconds
            .setFastestInterval((1 * 1000).toLong()) // 1 second, in milliseconds
        LocationUpdate()
        tvBack.setOnClickListener {
            rlyoutLocationHistory.visibility = View.VISIBLE
            rlyoutMapView.visibility = View.GONE
        }
    }

    private fun LocationUpdate() {
        val handler = Handler()
        handler.postDelayed({
            onConnected(tempsavedInstanceState)
            LocationUpdate()
        }, 10000)
    }

    private fun LoadList() {
        val cartList = RealmLibrary.instance.cartList
        if (cartList != null && cartList.size > 0) {
            rvList.visibility = View.VISIBLE
            @SuppressLint("WrongConstant") val layoutManager =
                LinearLayoutManager(this@LocationHistory, LinearLayoutManager.VERTICAL, false)
            rvList.layoutManager = layoutManager


            val adapter = AddressListAdapter(this@LocationHistory, cartList,  object : ListOnclick {
                override fun onClick(
                    lat: String?,
                    longt: String?,
                    address: String?,
                    time: String?
                ) {
                    rlyoutLocationHistory.visibility = View.GONE
                    rlyoutMapView.visibility = View.VISIBLE
                    if (lat != null) {
                        markerLatitude = lat.toDouble()
                    }
                    if (longt != null) {
                        markerLongitude = longt.toDouble()
                    }
                    if (address != null) {
                        str_address = address
                    }
                    if (time != null) {
                        str_time = time
                    }
                    LoadMapCartCustomer()

                }
            })
            rvList.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        mGoogleApiClient!!.connect()
        if (RealmLibrary.instance.allCartListSize > 0) {
            LoadList()
        }

    }

    override fun onPause() {
        super.onPause()
        Log.v(this.javaClass.simpleName, "onPause()")
        //Disconnect from API onPause()
        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.latitude
            currentLongitude = location.longitude

           //  Toast.makeText(this, "\n"+ currentLatitude+","+currentLongitude, Toast.LENGTH_LONG).show();
            addDataToDB(currentLatitude, currentLongitude)
        }
    }

    private fun addDataToDB(currentLatitude: Double, currentLongitude: Double) {
        current_address = CommonFunctions.getInstance().GeoCodingAddress_details(this@LocationHistory, currentLatitude, currentLongitude)
        val sdf = SimpleDateFormat("dd-MM-yyyy/HH:mm", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        val strings = currentDateandTime.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val time = strings[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        RealmLibrary.instance.insertItem(
            this,
            time[1],
            current_address,
            currentLatitude.toString() + "",
            currentLongitude.toString() + "",
            "1",
            currentDateandTime,
            currentDateandTime
        )
        LoadList()
        // Toast.makeText(LocationHistory.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST
                )
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (e: IntentSender.SendIntentException) {
                // Log the error
                e.printStackTrace()
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e(
                "Error",
                "Location services connection failed with code " + connectionResult.errorCode
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude
        //  Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

    private fun LoadMapCartCustomer() {
        binding!!.customMapView.onCreate(tempsavedInstanceState)
        binding!!.customMapView.onResume()
        try {
            MapsInitializer.initialize(this@LocationHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding!!.customMapView.getMapAsync(OnMapReadyCallback { mMap ->
            googleMap = mMap

            // Current Location button Adjust
            if (googleMap != null && binding!!.customMapView.findViewById<View?>("1".toInt()) != null) {
                val locationButton =
                    (binding!!.customMapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>(
                        "2".toInt()
                    )
                val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                layoutParams.setMargins(0, 0, 0, 0)
            }
            googleMap!!.uiSettings.isMyLocationButtonEnabled = false
            googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            val success = googleMap!!.setMapStyle(
                MapStyleOptions(
                    resources
                        .getString(R.string.style_json)
                )
            )
            if (!success) {
                Log.e("", "Style parsing failed.")
            }
            if (ActivityCompat.checkSelfPermission(
                    this@LocationHistory,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@LocationHistory,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@OnMapReadyCallback
            }
            googleMap!!.isMyLocationEnabled = false
            googleMap!!.clear()
            val point = LatLng(markerLatitude, markerLongitude)
            googleMap!!.uiSettings.isRotateGesturesEnabled = false
            val cameraPosition = CameraPosition.Builder()
                .target(point)
                .zoom(12f)
                .build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            val marker = googleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(markerLatitude, markerLongitude))
                    .title(str_address)
                    .snippet(str_time)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
            )
            marker!!.isDraggable = false
            marker.showInfoWindow()
        })
    }

    override fun onBackPressed() {}

    companion object {
        private const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    }
}