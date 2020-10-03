package com.doaha

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class AdminLogin : AppCompatActivity(){
    private val RC_SIGN_IN = 9001
    //private var mGoogleSignInClient: GoogleSignInClient? = null

    //Creation of Google sign-in object and add options
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        createSignIn()
/*        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.google_maps_key))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/
    }

    private fun createSignIn(){
        val providers = arrayListOf<AuthUI.IdpConfig>(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == RC_SIGN_IN){
            var response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                startActivity(Intent(this,AdminPage::class.java))
            }
            else{
                if (response == null){
                    finish()
                }
                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK){
                    return
                }
                if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR){
                    Toast.makeText(this, response?.error?.errorCode.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    //Link sign-in object and create on-click listener, once clicked launching google sign-in flow
    /*public override fun onStart() {
        super.onStart()
        val mGmailSignIn = findViewById<SignInButton>(R.id.sign_in_button)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        mGmailSignIn.setOnClickListener {
            signIn()
        }
    }

    //Verification of authenticated user
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    //Verify user that is signed in
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        val t = Toast.makeText(this, "Log in failed: Permission Denied", Toast.LENGTH_LONG)
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            if (completedTask.result!!.email == getString(R.string.admin_email)) {
                // go to admin page
                startActivity(Intent(this, AdminPage::class.java))
            } else {
                t.show()
            }
        } catch (e: ApiException) {
            t.show()
        }
    }

    //Google sign flow
    private fun signIn() {
        startActivityForResult(mGoogleSignInClient!!.signInIntent, RC_SIGN_IN)
    }*/
}