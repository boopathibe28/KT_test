package com.app.test.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.test.R
import com.app.test.databinding.ActivityLoginBinding
import com.app.test.utils.CommonFunctions
import com.app.test.utils.LanguageConstants
import com.app.test.utils._pref.SessionManager
import com.app.test.utils._pref.SharedPrefConstants
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import hari.bounceview.BounceView

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    var binding: ActivityLoginBinding? = null
    private var isPassword = false
    private var callbackManager: CallbackManager? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val is_kiosk_login_type: String? = null
    private val installer_type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        tempLoginActivity = this@LoginActivity
        initiView()
        callbackManager = CallbackManager.Factory.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(resources.getString(R.string.web_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
        signOut()
    }

    // ---- Initial View
    private fun initiView() {
        BounceView.addAnimTo(binding!!.txtLogin)
        BounceView.addAnimTo(binding!!.lyoutNewAccount)
        BounceView.addAnimTo(binding!!.txtForgotPassword)
        binding!!.txtLoginHeader.text = LanguageConstants.login
        binding!!.txtLoginSub.text = LanguageConstants.wereGladYour
        binding!!.txtUserName.text = LanguageConstants.emailId_mobileNumber
        binding!!.txtPassword.text = LanguageConstants.password
        binding!!.txtForgotPassword.text = LanguageConstants.forgotPasswordQ
        binding!!.txtLogin.text = LanguageConstants.logIn
        binding!!.txtOr.text = LanguageConstants.or
        binding!!.txtRegister.text = LanguageConstants.register_a
        binding!!.txtNewAccount.text = LanguageConstants.newAccount
        binding!!.txtForgotPassword.setOnClickListener(this)
        binding!!.txtLogin.setOnClickListener(this)
        binding!!.imgPasswordVG.setOnClickListener(this)
        binding!!.lyoutNewAccount.setOnClickListener(this)
        binding!!.fbLogin.setOnClickListener(this)
        binding!!.imgGoogle.setOnClickListener(this)
        binding!!.imgPasswordVG.setBackgroundResource(R.drawable.password_gone)
    }

    // --- OnClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgPasswordVG -> if (isPassword) {
                binding!!.imgPasswordVG.setBackgroundResource(R.drawable.password_gone)
                binding!!.edtPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                isPassword = false
                binding!!.edtPassword.setSelection(binding!!.edtPassword.text.length)
            } else {
                binding!!.imgPasswordVG.setBackgroundResource(R.drawable.password_visibility)
                binding!!.edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                isPassword = true
                binding!!.edtPassword.setSelection(binding!!.edtPassword.text.length)
            }

            R.id.lyoutNewAccount -> {}
            R.id.txtLogin -> if (binding!!.edtUserName.text.toString().trim { it <= ' ' }
                    .isEmpty()) {
                CommonFunctions.getInstance().validationEmptyError(
                    this@LoginActivity,
                    LanguageConstants.entervaildemailidandMobileNo
                )
            } else if (!CommonFunctions.getInstance()
                    .isValidEmail(binding!!.edtUserName.text.toString().trim { it <= ' ' })
            ) {
                CommonFunctions.getInstance()
                    .validationEmptyError(this@LoginActivity, LanguageConstants.entervaildemailid)
            } else if (binding!!.edtUserName.text.toString().trim { it <= ' ' }.length < 3) {
                CommonFunctions.getInstance().validationEmptyError(
                    this@LoginActivity,
                    LanguageConstants.entervaildemailidandMobileNo
                )
            } else if (binding!!.edtPassword.text.toString().trim { it <= ' ' }.isEmpty()) {
                CommonFunctions.getInstance()
                    .validationEmptyError(this@LoginActivity, LanguageConstants.passwordIsRequired)
            } else {
                loginApiCall()
            }

            R.id.fbLogin -> {
                signOutFB()
                binding!!.loginButton.performClick()
                binding!!.loginButton.setReadPermissions(mutableListOf("email"))
                binding!!.loginButton.registerCallback(
                    callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(loginResult: LoginResult) {
                            val GraphRequest =
                                GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                                    try {
                                        var email: String? = ""
                                        val birthday = ""
                                        var id = ""
                                        var firstName: String? = ""
                                        var lastName: String? = ""
                                        val imageUrl =
                                            "https://graph.facebook.com/$id/picture?type=large"
                                        try {
                                            email =
                                                if (`object`.getString("email") == null) "" else `object`.getString(
                                                    "email"
                                                )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        try {
                                            firstName =
                                                if (`object`.getString("first_name") == null) "" else `object`.getString(
                                                    "first_name"
                                                )
                                            lastName =
                                                if (`object`.getString("last_name") == null) "" else `object`.getString(
                                                    "last_name"
                                                )
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                        }
                                        try {
                                            id =
                                                if (`object`.getString("id") == null) "" else `object`.getString(
                                                    "id"
                                                )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        val token = AccessToken.getCurrentAccessToken()
                                        var mToken: String? = null
                                        if (token != null) {
                                            mToken = token.token
                                        }
                                        //  mobileNumberDialog(firstName,lastName,email,id,SOCIAL_LOGIN_TYPE_FB);
                                        //  socialLogin(firstName, lastName, email, id, "", SOCIAL_LOGIN_TYPE_FB);
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        LoginManager.getInstance().logOut()
                                    }
                                }
                            val parameters = Bundle()
                            binding!!.loginButton.setReadPermissions(mutableListOf("email"))
                            parameters.putString(
                                "fields",
                                "id, first_name, last_name, email, gender"
                            )
                            GraphRequest.parameters = parameters
                            GraphRequest.executeAsync()
                        }

                        override fun onCancel() {
                            // App code
                        }

                        override fun onError(exception: FacebookException) {
                            // App code
                        }
                    })
            }

            R.id.imgGoogle -> {
                signOut()
                binding!!.imLoginGoogle.performClick()
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            else -> {}
        }
    }

    // ------ Social Register
    private fun socialLogin(
        firstName: String,
        lastName: String,
        email: String,
        id: String,
        mobilenumber: String,
        register_type: String
    ) {
    }

    // -- Login Api
    private fun loginApiCall() {
        SessionManager.getInstance()
            .insertIntoPreference(this@LoginActivity, SharedPrefConstants.ACCESS_TOKEN, "123456")
        CommonFunctions.getInstance().newIntent(this@LoginActivity, LocationHistory::class.java, Bundle.EMPTY, true, true)
    }

    private fun signOutFB() {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null) {
            LoginManager.getInstance().logOut()
        }
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this@LoginActivity) { }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            if (completedTask.isSuccessful) {
                val account = completedTask.getResult(
                    ApiException::class.java
                )
                Log.e("Gmail LogId", account.id!!)

                //mobileNumberDialog(account.getDisplayName(),"",account.getEmail(),account.getId(),SOCIAL_LOGIN_TYPE_GOOGLE);
                //signUp(account.getDisplayName(), account.getId(), account.getEmail());
                //socialLogin(account.getDisplayName(), "", account.getEmail(), account.getId(), "", SOCIAL_LOGIN_TYPE_GOOGLE);
            }
            SessionManager.getInstance().insertIntoPreference(
                this@LoginActivity,
                SharedPrefConstants.ACCESS_TOKEN,
                "123456"
            )
            CommonFunctions.getInstance()
                .newIntent(this@LoginActivity, LocationHistory::class.java, Bundle.EMPTY, true, true)
        } catch (e: ApiException) {
            Log.w("statusCode", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(o: Any?) {
        val account = o as GoogleSignInAccount?
        return
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        var tempLoginActivity: LoginActivity? = null
    }
}