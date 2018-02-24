package com.togglecorp.paiso.auth

import android.content.Context
import android.preference.PreferenceManager
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.promise.Promise
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

object Auth {
    fun getToken(context: Context) : String? =
        PreferenceManager.getDefaultSharedPreferences(context)
                .getString("token", null)

    fun getHeader(context: Context) = "Token ${getToken(context)}"

    fun getUsername(context: Context) : String? =
            PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("username", null)

    fun storeToken(context: Context, username: String?, token: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("token", token)
                .putString("username", username)
                .apply()
    }

    fun attemptLogin(context: Context, username: String, password: String) : Promise<Unit?> {
        return AuthApi.authenticate(Credentials(username, password)).promise()
                .then {
                    storeToken(context, username, it?.body()?.token)
                }
    }

    fun register(
            context: Context,
            first_name: String,
            last_name: String,
            username: String,
            password: String
    ) : Promise<Unit?> {
        return AuthApi.register(Registration(first_name, last_name, username, password)).promise()
                .thenPromise { attemptLogin(context, username, password) }
    }

    fun  logout(context: Context) {
        Auth.storeToken(context, null, null)
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("myRemoteId", null)
                .apply()

        async(CommonPool) {
            DatabaseContext.get(context).userDao().deleteAll()
            DatabaseContext.get(context).transactionDao().deleteAll()
            DatabaseContext.get(context).contactDao().deleteAll()
            DatabaseContext.get(context).expenseDao().deleteAll()
        }
    }
}

