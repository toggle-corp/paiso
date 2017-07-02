package com.togglecorp.paiso.api

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.togglecorp.paiso.promise.Promise
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors


object Api {
    val gson = GsonConverterFactory.create(GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create())!!

    val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.11:8000/")
//            .baseUrl("http://192.168.100.30:8000/")
            .addConverterFactory(gson)
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()!!

}

fun <T> Call<T>.promise() : Promise<Response<T>?> {
    val promise = Promise<Response<T>?>()

    this.enqueue(object: Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            if (response != null && response.isSuccessful) {
                promise.resolve(response)
            } else {
                promise.reject(Exception(response?.errorBody()?.string()))
            }
        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {
            promise.reject(t)
        }
    })

    return promise
}