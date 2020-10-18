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
import com.google.firebase.auth.FirebaseAuth

class AdminLogin : AppCompatActivity(){
    private val RC_SIGN_IN = 9001

    //Creation of Google sign-in object and add options
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        createSignIn()
    }

    private fun createSignIn(){
        val providers = arrayListOf<AuthUI.IdpConfig>(
        AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
        println("TEST3")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == RC_SIGN_IN){
            val t = Toast.makeText(this, "Log in failed: Permission Denied", Toast.LENGTH_LONG)
            var response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                if (user?.email == "doaha2020@gmail.com"){
                    startActivity(Intent(this,AdminPage::class.java))
                }
                else {
                    t.show()
                }
            }
            else{
                if (response == null){
                    finish()
                }
                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK){
                    return
                }
                if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR){
                    Toast.makeText(this, response.error?.errorCode.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}