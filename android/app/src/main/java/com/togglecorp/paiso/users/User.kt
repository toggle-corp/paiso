package com.togglecorp.paiso.users

import android.arch.persistence.room.*
import android.arch.persistence.room.util.StringUtil
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.togglecorp.paiso.api.Api
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

@Entity(indices = arrayOf(Index(value = "remoteId", unique = true)))
data class User (
        @PrimaryKey(autoGenerate = true)
        var _id: Int? = null,

        @SerializedName("id")
        var remoteId: Int? = null,

        var firstName: String? = null,
        var lastName: String? = null,
        var username: String = "",

        var sync: Boolean = false
) {
    fun getName() = "${firstName} ${lastName}"
}

@Dao
interface UserDao {
    @Insert
    fun insert(user: User) : Long

    @Query("SELECT * FROM user")
    fun getAll() : List<User>

    @Query("SELECT * FROM user WHERE username = :arg0 LIMIT 1")
    fun findByUserName(username: String) : User?

    @Query("SELECT * FROM user WHERE _id = :arg0 LIMIT 1")
    fun findById(id: Int) : User?

    @Query("SELECT * FROM user WHERE remoteId = :arg0 LIMIT 1")
    fun findByRemoteId(remoteId: Int) : User?

    @Query("DELETE FROM user")
    fun deleteAll()

    @Query("DELETE FROM user WHERE username = :arg0")
    fun delete(username: String)
}


interface IUserApi {
    @GET("user/me/")
    fun getMe(@Header("Authorization") header: String) : Call<User>

    @GET("user/")
    fun  search(@Header("Authorization") header: String, @retrofit2.http.Query("q") query: String) : Call<List<User>>

    @GET("user/{id}/")
    fun  get(@Header("Authorization") header: String, @Path("id") id: Int) : Call<User>
}

val UserApi = Api.retrofit.create(IUserApi::class.java)!!