package com.doaha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class AdminLogin : AppCompatActivity(){
    private val RC_SIGN_IN = 9001
    private var mGoogleSignInClient: GoogleSignInClient? = null

    //Creation of Google sign-in object and add options
    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate START")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_maps_key))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        println("onCreate END")
    }

    //Link sign-in object and create on-click listener, once clicked launching google sign-in flow
    public override fun onStart() {
        println("onSTART START")
        super.onStart()
        val mGmailSignIn = findViewById<SignInButton>(R.id.sign_in_button)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        println("onSTART STARTSign In: " + account.toString())
        mGmailSignIn.setOnClickListener {
            signIn()
        }
        println("onSTART END")
    }

    //Verificiation of authenticated user
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("onActivityResult START")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        println("onActivityResult END")
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        println("handleSignInResult START")
        println("Sign In: "+ completedTask.toString())
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            println("Sign In: " + idToken.toString())
        } catch (e: ApiException) {
            println("Sign In: "+ "signInResult:failed code=" + e.statusCode)
        }
        println("handleSignInResult END")
    }
    private fun signIn() {
        println("signIn START")
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        println("signIn END")
    }
}