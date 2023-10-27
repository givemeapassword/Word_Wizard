package com.example.wordwizard.db

import android.provider.BaseColumns

object MyDbNameClass:BaseColumns {

    const val TABLE_NAME = "Card_table"
    const val COLUMN_NAME_TEXT = "Card_text"
    const val COLUMN_NAME_IMAGE = "Card_image"

    const val DATA_BASE_NAME = "CardReader.db"
    const val DATABASE_VERSION = 1

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME ${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "$COLUMN_NAME_IMAGE TEXT, $COLUMN_NAME_TEXT TEXT)"

    const val DROP_TABLE =
        "DROP TABLE IF EXISTS $TABLE_NAME"

    const val SELECT_ALL =
        "SELECT * FROM $TABLE_NAME"
}