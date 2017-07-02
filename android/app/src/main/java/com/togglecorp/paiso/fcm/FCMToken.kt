package com.togglecorp.paiso.fcm

import com.togglecorp.paiso.api.Api
import retrofit2.Call
import retrofit2.http.*

data class FCMToken (
        var id: Int? = null,
        var token: String? = null,
        var user: Int? = null
)

interface IFCMTokenApi {
    @GET("fcm-token/")
    fun get(@Header("Authorization") header: String) : Call<List<FCMToken>>

    @PUT("fcm-token/{id}/")
    fun put(@Header("Authorization") header: String, @Path("id") id: Int, @Body token: FCMToken) : Call<FCMToken>

    @POST("fcm-token/")
    fun post(@Header("Authorization") header: String, @Body token: FCMToken) : Call<FCMToken>
}

val FCMTokenApi = Api.retrofit.create(IFCMTokenApi::class.java)