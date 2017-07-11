package com.togglecorp.paiso.auth

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.togglecorp.paiso.R
import com.togglecorp.paiso.MainActivity
import kotlinx.android.synthetic.main.activity_login.*

const val ACCOUNT_TYPE = "paiso.togglecorp.com"
const val AUTH_TOKEN_TYPE = "user"

class LoginActivity : AccountAuthenticatorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkAuthenticated()
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticated()
    }

    fun checkAuthenticated() {
        val authToken = Auth.getToken(this)
        val username = Auth.getUsername(this)
        if (authToken != null && username != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun register(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun login(view: View) {
        if (TextUtils.isEmpty(username.text)) {
            username_layout.error = "Enter your username"
            return
        }

        if (TextUtils.isEmpty(password.text)) {
            password_layout.error = "Enter your password"
            return
        }

        username_layout.error = null
        password_layout.error = null

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging in")
        progressDialog.show()

        Auth.attemptLogin(this, username.text.toString(), password.text.toString())
                .catch {
                    runOnUiThread {
                        Log.d("Login Activity", it?.localizedMessage)
                        Toast.makeText(this, "Failed to log in. Make sure you are connected to internet.", Toast.LENGTH_SHORT).show()
                    }
                }
                .then {
                    runOnUiThread {
                        progressDialog.dismiss()
                        checkAuthenticated()
                    }
                }
    }
}
