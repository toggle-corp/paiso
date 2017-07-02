package com.togglecorp.paiso.transaction

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.Query
import android.content.Context
import com.google.gson.annotations.SerializedName
import com.togglecorp.paiso.api.Api
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.database.DatabaseContext
import retrofit2.Call
import retrofit2.http.*
import java.util.*


@Entity(tableName = "paiso_transaction", indices = arrayOf(Index(value = "remoteId", unique = true)))
data class PaisoTransaction(
        @PrimaryKey(autoGenerate = true)
        var _id: Int? = null,

        @SerializedName("id")
        var remoteId: Int? = null,

        var user: Int? = null,
        var transactionType: String = "to",
        var contact: Int? = null,

        var title: String = "",
        var amount: Float = 0.0f,

        var createdAt: Date = Date(),
        var editedAt: Date = Date(),

        var acknowledgedAt: Date? = null,
        var status: String = "pending",
        var deleted: Boolean = false,

        var sync: Boolean = false
) {
    fun saveAsSynchronized(context: Context) {
        sync = true
        DatabaseContext.get(context).transactionDao().update(this)
    }
}

@Dao
interface TransactionDao {
    @Insert
    fun insert(transaction: PaisoTransaction)

    @Update
    fun update(vararg transactions: PaisoTransaction)

    @Query("DELETE FROM `paiso_transaction`")
    fun deleteAll()

    @Query("SELECT * FROM `paiso_transaction`")
    fun getAll() : LiveData<List<PaisoTransaction>>

    @Query("SELECT * FROM `paiso_transaction` WHERE sync = 0")
    fun getModified() : LiveData<List<PaisoTransaction>>

    @Query("SELECT * FROM `paiso_transaction` WHERE remoteId = :arg0 LIMIT 1")
    fun findByRemoteId(remoteId: Int?) : PaisoTransaction?

    @Query("SELECT * FROM `paiso_transaction` WHERE user = :arg1 OR contact = :arg0")
    fun  getFor(contactId: Int?, userId: Int?): LiveData<List<PaisoTransaction>>
}


interface ITransactionApi {
    @GET("transaction/")
    fun get(@Header("Authorization") header: String) : Call<List<PaisoTransaction>>

    @PUT("transaction/{id}/")
    fun put(@Header("Authorization") header: String, @Path("id") id: Int, @Body transaction: PaisoTransaction) : Call<PaisoTransaction>

    @POST("transaction/")
    fun post(@Header("Authorization") header: String, @Body transaction: PaisoTransaction) : Call<PaisoTransaction>
}

val TransactionApi = Api.retrofit.create(ITransactionApi::class.java)!!



