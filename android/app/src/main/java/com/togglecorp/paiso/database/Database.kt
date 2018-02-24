package com.togglecorp.paiso.database

import android.arch.persistence.room.*
import android.content.Context
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.contacts.ContactDao
import com.togglecorp.paiso.expenses.Expense
import com.togglecorp.paiso.expenses.ExpenseDao
import com.togglecorp.paiso.transactions.PaisoTransaction
import com.togglecorp.paiso.transactions.TransactionDao
import com.togglecorp.paiso.users.User
import com.togglecorp.paiso.users.UserDao
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = arrayOf(Contact::class, User::class, PaisoTransaction::class, Expense::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun contactAmountDao(): ContactAmountDao
}

class DatabaseContext private constructor(context: Context){
    companion object {
        private var databaseContext: DatabaseContext? = null

        fun getInstance(context: Context) : DatabaseContext {
            if (databaseContext == null) {
                databaseContext = DatabaseContext(context.applicationContext)
            }
            return databaseContext!!
        }

        fun get(context: Context) : AppDatabase = getInstance(context).database
    }

    val database = Room.databaseBuilder(context, AppDatabase::class.java, "paiso-db").build()!!
}
