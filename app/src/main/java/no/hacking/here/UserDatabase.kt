package no.hacking.here

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import no.hacking.here.DatabaseHelper.DatabaseConstants.ID
import no.hacking.here.DatabaseHelper.DatabaseConstants.PASSWORD
import no.hacking.here.DatabaseHelper.DatabaseConstants.USERNAME
import no.hacking.here.DatabaseHelper.DatabaseConstants.USERS
import java.lang.Exception

class UserDatabase(context: Context) {

    private val dbManager: DatabaseHelper = DatabaseHelper(context)
    private val database: SQLiteDatabase = dbManager.writableDatabase

    @Throws(Exception::class)
    fun storeUser(id: Int, name: String, password: String): Long {
        val values = ContentValues()
        values.put(ID, id)
        values.put(USERNAME, name)
        values.put(PASSWORD, password)
        return database.insert(USERS, null, values)
    }

    @Throws(Exception::class)
    fun userExists(userName: String): Boolean {
        val query = "SELECT EXISTS (SELECT * FROM $USERS WHERE $USERNAME='$userName' LIMIT 1)"
        return valueExists(database.rawQuery(query, null))
    }

    @Throws(Exception::class)
    fun passwordisCorrect(userName: String, password: String): Boolean {
        val query = "SELECT EXISTS (SELECT * FROM $USERS WHERE $USERNAME='$userName' AND $PASSWORD='$password' LIMIT 1)"
        return valueExists(database.rawQuery(query, null))
    }

    @Throws(Exception::class)
    fun valueExists(cursor: Cursor): Boolean {
        cursor.moveToFirst()
        return if (cursor.getInt(0) == 1) {
            cursor.close()
            true
        } else {
            cursor.close()
            false
        }
    }

//        cursor?.let {
//            it.moveToFirst()
//            val user = User(
//                it.getInt(it.getColumnIndex(ID)),
//                it.getString(it.getColumnIndex(USERNAME)),
//                it.getString(it.getColumnIndex(PASSWORD))
//            )
//            it.close()
//        } ?:
}

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, NAME, null, VERSION) {

    companion object DatabaseConstants {
        const val NAME = "BankUserDatabase.db"
        const val VERSION = 1
        const val USERS = "Users"
        const val ID = "_id"
        const val USERNAME = "userName"
        const val PASSWORD = "password"
        const val CREATE = "CREATE TABLE $USERS(_id integer PRIMARY KEY, $USERNAME text NOT NULL, $PASSWORD text NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.e(this.javaClass.simpleName, "Upgrading database from version $oldVersion to $newVersion, which will destroy all old data")
        db?.execSQL("DROP TABLE IF EXISTS $USERS")
        onCreate(db)
    }
}

data class User(val id: Int, val userName: String, val password: String)