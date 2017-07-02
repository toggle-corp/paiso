package com.togglecorp.paiso.contacts

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.Query
import android.content.Context
import com.google.gson.annotations.SerializedName
import com.togglecorp.paiso.api.Api
import com.togglecorp.paiso.database.DatabaseContext
import retrofit2.Call
import retrofit2.http.*
import java.util.*


@Entity(indices = arrayOf(Index(value = "remoteId", unique = true)))
data class Contact (
        @PrimaryKey(autoGenerate = true)
        var _id: Int? = null,

        @SerializedName("id")
        var remoteId: Int? = null,

        var name: String = "",
        var user: Int? = null,

        var createdAt: Date = Date(),
        var editedAt: Date = Date(),

        var sync: Boolean = false,
        var deleted: Boolean = false
) {
    fun saveAsSynchronized(context: Context) {
        sync = true
        DatabaseContext.get(context).contactDao().update(this)
    }
}

@Dao
interface ContactDao {
    @Insert
    fun insert(vararg contacts: Contact)

    @Query("SELECT * FROM contact")
    fun getAll(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE sync = 0")
    fun getModified() : LiveData<List<Contact>>

    @Query("SELECT * FROM contact WHERE remoteId = :arg0 LIMIT 1")
    fun findByRemoteId(remoteId: Int?) : Contact?

    @Query("SELECT * FROM contact WHERE user = :arg0 LIMIT 1")
    fun findByUserId(userId: Int?) : Contact?

    @Query("SELECT * FROM contact WHERE _id = :arg0 LIMIT 1")
    fun  findById(_id: Int?): LiveData<Contact>

    @Query("SELECT * FROM contact WHERE remoteId = :arg0 LIMIT 1")
    fun  findLiveByRemoteId(_id: Int?): LiveData<Contact>

    @Update
    fun update(vararg contacts: Contact)

    @Query("DELETE FROM contact")
    fun deleteAll()
}

interface IContactApi {
    @GET("contact/")
    fun get(@Header("Authorization") header: String) : Call<List<Contact>>

    @PUT("contact/{id}/")
    fun put(@Header("Authorization") header: String, @Path("id") id: Int, @Body contact: Contact) : Call<Contact>

    @POST("contact/")
    fun post(@Header("Authorization") header: String, @Body contact: Contact) : Call<Contact>

    @DELETE("contact/{id}/")
    fun delete(@Header("Authorization") header: String, @Path("id") id: Int) : Call<Void>
}

val ContactApi = Api.retrofit.create(IContactApi::class.java)!!

