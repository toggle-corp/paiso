package com.togglecorp.paiso.auth

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.togglecorp.paiso.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        checkAuthenticated()
    }

    fun checkAuthenticated() {
        if (Auth.getToken(this) != null) {
            finish()
        }
    }

    fun register(view: View) {
        if (TextUtils.isEmpty(first_name.text)) {
            first_name_container.error = "Enter first name"
            return
        }

        if (TextUtils.isEmpty(last_name.text)) {
            last_name_container.error = "Enter last name"
            return
        }

        if (TextUtils.isEmpty(username.text)) {
            username_container.error = "Enter a user name"
            return
        }

        if (TextUtils.isEmpty(password.text)) {
            password_container.error = "Enter a password"
            return
        }

        if (!TextUtils.equals(password.text, repassword.text)) {
            repassword_container.error = "Your passwords do not match"
            return
        }

        first_name_container.error = null
        last_name_container.error = null
        username_container.error = null
        password_container.error = null
        repassword_container.error = null

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing up")
        progressDialog.show()


        Auth.register(
                this,
                first_name.text.toString(),
                last_name.text.toString(),
                username.text.toString(),
                password.text.toString()
        )
                .catch {
                    Log.d("Register Activity", it?.localizedMessage)
                    Toast.makeText(this, "Failed to register. Make sure you are connected to internet.", Toast.LENGTH_SHORT).show()
                }
                .then {
                    progressDialog.dismiss()
                    checkAuthenticated()
                }
    }
}