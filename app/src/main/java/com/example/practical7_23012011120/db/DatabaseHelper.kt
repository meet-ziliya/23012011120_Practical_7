package com.example.practical7_23012011120.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.practical7_23012011120.Person

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DB_VERSION) {

    companion object {
        const val DATABASE_NAME = "person_db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(PersonDbTable.CREATE_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS " + PersonDbTable.TABLE_NAME)
        }
        onCreate(db)
    }

    private fun getValues(person: Person): ContentValues {
        val values = ContentValues()

        values.put(PersonDbTable.COLUMN_ID, person.id)
        values.put(PersonDbTable.COLUMN_PERSON_NAME, person.name)
        values.put(PersonDbTable.COLUMN_PERSON_EMAIL_ID, person.emailId)
        values.put(PersonDbTable.COLUMN_PERSON_PHONE_NO, person.phoneNo)
        values.put(PersonDbTable.COLUMN_PERSON_ADDRESS, person.address)

        return values
    }

    private fun getPerson(cursor: Cursor): Person {
        return Person(
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_EMAIL_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_PHONE_NO)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_ADDRESS))
        )
    }

    fun getPerson(id: String): Person? {
        val db = this.readableDatabase
        val cursor = db.query(
            PersonDbTable.TABLE_NAME,
            arrayOf(
                PersonDbTable.COLUMN_ID,
                PersonDbTable.COLUMN_PERSON_NAME,
                PersonDbTable.COLUMN_PERSON_EMAIL_ID,
                PersonDbTable.COLUMN_PERSON_PHONE_NO,
                PersonDbTable.COLUMN_PERSON_ADDRESS
            ),
            PersonDbTable.COLUMN_ID + "=?",
            arrayOf(id),
            null,
            null,
            null,
            null
        ) ?: return null

        cursor.moveToFirst()

        if (cursor.count == 0)
            return null

        val person = getPerson(cursor)
        cursor.close()
        return person
    }

    val allPersons: ArrayList<Person>
        get() {
            val persons = ArrayList<Person>()

            val selectQuery = "SELECT * FROM " +
                    PersonDbTable.TABLE_NAME +
                    " ORDER BY " +
                    PersonDbTable.COLUMN_PERSON_NAME +
                    " DESC"

            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    persons.add(getPerson(cursor))
                } while (cursor.moveToNext())
            }

            cursor.close()
            // ❌ Do NOT close db here
            return persons
        }

    val personsCount: Int
        get() {
            val countQuery = "SELECT * FROM " + PersonDbTable.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)

            val count = cursor.count
            cursor.close()
            return count
        }

    fun updatePerson(person: Person): Int {
        val db = this.writableDatabase
        return db.update(
            PersonDbTable.TABLE_NAME,
            getValues(person),
            PersonDbTable.COLUMN_ID + " = ?",
            arrayOf(person.id)
        )
    }

    fun deletePerson(person: Person) {
        val db = this.writableDatabase
        db.delete(
            PersonDbTable.TABLE_NAME,
            PersonDbTable.COLUMN_ID + " = ?",
            arrayOf(person.id)
        )
        // ❌ do NOT close db
    }

    fun insertPerson(person: Person): Long {
        val db = this.writableDatabase
        return db.insert(PersonDbTable.TABLE_NAME, null, getValues(person))
        // ❌ do NOT close db
    }
}
