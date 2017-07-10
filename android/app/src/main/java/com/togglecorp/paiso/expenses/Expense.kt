package com.togglecorp.paiso.expenses

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
data class Expense (
        @PrimaryKey(autoGenerate = true)
        var _id: Int? = null,

        @SerializedName("id")
        var remoteId: Int? = null,

        var user: Int? = null,
        var title: String = "",
        var amount: Float = 0.0f,

        var createdAt: Date = Date(),
        var editedAt: Date = Date(),
        var deleted: Boolean = false,

        var sync: Boolean = false
) {
    fun saveAsSynchronized(context: Context) {
        sync = true
        DatabaseContext.get(context).expenseDao().update(this)
    }
}


@Dao
interface ExpenseDao {
    @Insert
    fun insert(expense: Expense)

    @Update
    fun update(vararg expenses: Expense)

    @Query("DELETE FROM expense")
    fun deleteAll()

    @Query("SELECT * FROM expense")
    fun getAll() : LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE deleted = 0")
    fun getExisting() : LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE deleted = 0 AND editedAt >= :arg0 AND editedAt <= :arg1")
    fun getExisting(startDate: Date, endDate: Date) : LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE sync = 0")
    fun getModified() : LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE _id = :arg0 LIMIT 1")
    fun findById(id: Int) : LiveData<Expense>

    @Query("SELECT * FROM expense WHERE remoteId = :arg0 LIMIT 1")
    fun findByRemoteId(remoteId: Int?) : Expense?
}

interface IExpenseApi {
    @GET("expense/")
    fun get(@Header("Authorization") header: String): Call<List<Expense>>

    @GET("expense/{id}/")
    fun get(@Header("Authorization") header: String, @Path("id") id: Int): Call<List<Expense>>

    @PUT("expense/{id}/")
    fun put(@Header("Authorization") header: String, @Path("id") id: Int, @Body expense: Expense): Call<Expense>

    @POST("expense/")
    fun post(@Header("Authorization") header: String, @Body expense: Expense): Call<Expense>
}

val ExpenseApi = Api.retrofit.create(IExpenseApi::class.java)!!