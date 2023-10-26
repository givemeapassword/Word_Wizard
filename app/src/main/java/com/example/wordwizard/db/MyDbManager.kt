package com.example.wordwizard.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.sql.Blob

class MyDbManager(context: Context) {
    val MyDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = MyDbHelper.writableDatabase
    }
    fun insertToDb( text: String, image: ByteArray){
        val values = ContentValues().apply{
            put(MydbNameClass.COLUMN_NAME_TEXT,text)
            put(MydbNameClass.COLUMN_NAME_IMAGE,image)
        }
        db?.insert(MydbNameClass.TABLE_NAME,null,values)
    }
    @SuppressLint("Range")
    fun readDbData(): ArrayList<String>{
        val dataList = ArrayList<String>()
        val cursor = db?.query(MydbNameClass.TABLE_NAME,null,null,null,
            null,null,null)
        while (cursor?.moveToNext()!!){
            val dataText = cursor.getString(cursor.getColumnIndex(MydbNameClass.COLUMN_NAME_TEXT))
            dataList.add(dataText.toString())
        }
        cursor.close()
        return dataList
    }

    fun closeDb(){
        MyDbHelper.close()
    }
}