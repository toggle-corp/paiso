package com.togglecorp.paiso.transactions

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.Query
import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.annotations.SerializedName
import com.togglecorp.paiso.api.Api
import com.togglecorp.paiso.database.DatabaseContext
import retrofit2.Call
import retrofit2.http.*
import java.util.*


@Entity(tableName = "paiso_transaction", indices = arrayOf(Index(value = "remoteId", unique = true), Index(value = "uuid", unique = true)))
data class PaisoTransaction(
        @PrimaryKey(autoGenerate = true)
        var _id: Int? = null,

        @SerializedName("id")
        var remoteId: Int? = null,

        var uuid: String = UUID.randomUUID().toString(),
        var version: Int = 0,

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

    fun isMy(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("myRemoteId",
                0) == user
    }

    fun  getType(context: Context) : String {
        if (isMy(context)) {
            return transactionType
        } else {
            return if (transactionType == "to") "by" else "to"
        }
    }

    fun getSignedAmount(context: Context) : Float {
        return if (getType(context) == "to") amount else -amount
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

    @Query("SELECT * FROM `paiso_transaction` WHERE sync = 0")
    fun getModifiedList() : List<PaisoTransaction>

    @Query("SELECT * FROM `paiso_transaction` WHERE _id = :arg0 LIMIT 1")
    fun findById(id: Int) : LiveData<PaisoTransaction>

    @Query("SELECT * FROM `paiso_transaction` WHERE remoteId = :arg0 LIMIT 1")
    fun findByRemoteId(remoteId: Int?) : PaisoTransaction?

    @Query("SELECT * FROM `paiso_transaction` WHERE uuid = :arg0 LIMIT 1")
    fun findByUuid(remoteId: String?) : PaisoTransaction?

    @Query("SELECT * FROM `paiso_transaction` WHERE ((user = :arg1 AND status = 'approved') OR contact = :arg0) AND deleted = 0")
    fun getFor(contactId: Int?, userId: Int?) : LiveData<List<PaisoTransaction>>

    @Query("SELECT * FROM `paiso_transaction` WHERE user != :arg0 AND ((acknowledgedAt IS NULL AND deleted = 0) OR acknowledgedAt < editedAt)")
    fun getNotifiable(userId: Int) : LiveData<List<PaisoTransaction>>

    @Query("DELETE FROM `paiso_transaction` WHERE user = :arg1 OR contact = :arg0")
    fun deleteFor(contactId: Int?, userId: Int?)
}


interface ITransactionApi {
    @GET("transaction/")
    fun get(@Header("Authorization") header: String) : Call<List<PaisoTransaction>>

    @GET("transaction/{id}/")
    fun get(@Header("Authorization") header: String, @Path("id") id: Int) : Call<PaisoTransaction>

    @PUT("transaction/{id}/")
    fun put(@Header("Authorization") header: String, @Path("id") id: Int, @Body transaction: PaisoTransaction) : Call<PaisoTransaction>

    @POST("transaction/")
    fun post(@Header("Authorization") header: String, @Body transaction: PaisoTransaction) : Call<PaisoTransaction>
}

val TransactionApi = Api.retrofit.create(ITransactionApi::class.java)!!



