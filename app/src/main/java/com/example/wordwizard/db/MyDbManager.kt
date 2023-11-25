package com.example.wordwizard.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.wordwizard.card.CardData

class MyDbManager(context: Context) {
    private val MyDbHelper = MyDbHelper(context)
    private var db: SQLiteDatabase? = null

    fun openDb(){
        db = MyDbHelper.writableDatabase
    }
    fun insertToDb( text: String, image: String, time: String){
        val values = ContentValues().apply{
            put(MyDbNameClass.COLUMN_NAME_TEXT,text)
            put(MyDbNameClass.COLUMN_NAME_IMAGE,image)
            put(MyDbNameClass.COLUMN_NAME_DATA,time)
        }
        db?.insert(MyDbNameClass.TABLE_NAME,null,values)
    }

    fun deleteFromDb(id: String){
        openDb()
        db?.delete(MyDbNameClass.TABLE_NAME,"_id=${id}",null)
        closeDb()
    }

    fun getAllCards(): ArrayList<CardData> {
        val cards = ArrayList<CardData>()
        val db = MyDbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(MyDbNameClass.SELECT_ALL, null)

        with(cursor) {
            if (moveToFirst()) {
                do {
                    val text = getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TEXT))
                    val image = getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_IMAGE))
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                    val data = getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_DATA))
                    val card = CardData(image, text, id, data)
                    cards.add(card)
                } while (moveToNext())
            }
            close()
        }


        db.close()
        return cards
    }

    fun closeDb(){
        MyDbHelper.close()
    }
}