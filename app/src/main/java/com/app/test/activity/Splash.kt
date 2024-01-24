package com.app.test.activity

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.test.databinding.ActivitySplashBinding
import com.app.test.utils.CommonFunctions
import com.app.test.utils.LanguageConstants
import com.app.test.utils._pref.SessionManager
import com.app.test.utils._pref.SharedPrefConstants.ACCESS_TOKEN
import com.google.android.material.snackbar.Snackbar
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Splash : AppCompatActivity(), View.OnClickListener  {
    lateinit var binding: ActivitySplashBinding
    var SPLASH_TIME: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(getLayoutInflater())
        val view: View = binding.root
        setContentView(view)

        checkInternetConnection()
    }

    override fun onClick(p0: View?) {


    }


    private fun checkInternetConnection() {
        //Check internet connection
        if (CommonFunctions.CheckInternetConnection()) {
            splash()
        } else {
            CommonFunctions.getInstance()
                .validationError(this@Splash, LanguageConstants.kindly_check_internet)
        }
    }


    private fun splash() {
        // Check Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(
                    this@Splash,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        + ContextCompat.checkSelfPermission(
                    this@Splash,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                        + ContextCompat.checkSelfPermission(
                    this@Splash,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@Splash,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this@Splash,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this@Splash,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    Snackbar.make(
                        findViewById<View>(R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                        "ENABLE"
                    ) {
                        ActivityCompat.requestPermissions(
                            this@Splash,
                            arrayOf<String>(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            999
                        )
                    }.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this@Splash, arrayOf<String>(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 999
                    )
                }
            } else {
                LoadSplash()
            }
        } else {
            LoadSplash()
        }
        generateKeyHash()
    }


    fun LoadSplash() {
        val handler = Handler()
        handler.postDelayed({ LoadNext() }, SPLASH_TIME)
    }


    // ---- Load Next
    fun LoadNext() {
        if (!SessionManager.getInstance().getFromPreference(ACCESS_TOKEN).isEmpty()) {
            CommonFunctions.getInstance().newIntent(this@Splash, LocationHistory::class.java, Bundle.EMPTY, true, true)
        }
        else {
            CommonFunctions.getInstance()
                .newIntent(this@Splash, LoginActivity::class.java, Bundle.EMPTY, true, true)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            999 -> {
                if (grantResults.size > 0 && grantResults[0] +
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    LoadSplash()
                } else {
                    val snackBarView = Snackbar.make(
                        findViewById(R.id.content), "Enable Permissions from settings",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                        "ENABLE"
                    ) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("package:$packageName")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(intent)
                    }
                    //                    CommonFunctions.getInstance().displaySnackBarWithBottomMargin(snackBarView, 0, 100);
                    snackBarView.show()
                }
                return
            }
        }
    }

    // ------ generateKeyHash
    private fun generateKeyHash() {
        try {
            @SuppressLint("PackageManagerGetSignatures") val info = packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

}