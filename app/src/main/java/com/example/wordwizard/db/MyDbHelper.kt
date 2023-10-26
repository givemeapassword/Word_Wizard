package com.example.wordwizard.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context): SQLiteOpenHelper(context,MydbNameClass.TABLE_NAME,
    null, MydbNameClass.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MydbNameClass.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(MydbNameClass.DROP_TABLE)
        onCreate(db)
    }

}