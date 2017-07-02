package com.togglecorp.paiso.auth

import com.togglecorp.paiso.api.Api
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthToken(
        val token: String
)

data class Credentials(
        val username: String,
        val password: String
)

data class Registration(
        val first_name: String,
        val last_name: String,
        val username: String,
        val password: String
)

interface IAuthApi {
    @POST("api-token-auth/")
    fun authenticate(@Body credentials: Credentials) : Call<AuthToken>

    @POST("user/")
    fun register(@Body registration: Registration) : Call<Void>
}

val AuthApi = Api.retrofit.create(IAuthApi::class.java)!!