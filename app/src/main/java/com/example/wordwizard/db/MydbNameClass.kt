package com.example.wordwizard.db

import android.provider.BaseColumns

object MydbNameClass {

    const val TABLE_NAME = "Card_table"
    const val COLUMN_NAME_TEXT = "Card_table"
    const val COLUMN_NAME_IMAGE = "Card_table"

    const val DATA_BASE_NAME = "CardReader.db"
    const val DATABASE_VERSION = 1

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME " +
            "(${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_IMAGE BLOB" +
            "$COLUMN_NAME_TEXT TEXT)"
    const val DROP_TABLE =
        "DROP TABLE IF EXISTS $TABLE_NAME"
}