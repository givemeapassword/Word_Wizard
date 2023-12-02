package com.example.wordwizard.db

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.provider.BaseColumns
import androidx.core.net.toUri
import com.example.wordwizard.card.CardData
import java.io.File

class MyDbManager(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    private lateinit var db: SQLiteDatabase

    private fun openDb(){ db = myDbHelper.writableDatabase }
    fun insertToDb(text: String, image: String, time: String, imageMipmap: ByteArray){
        openDb()
        val values = ContentValues().apply{
            put(MyDbNameClass.COLUMN_NAME_TEXT, text)
            put(MyDbNameClass.COLUMN_NAME_IMAGE, image)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_MIPMAP, imageMipmap)
            put(MyDbNameClass.COLUMN_NAME_DATA, time)
        }
        db.insert(MyDbNameClass.TABLE_NAME, null, values)
        closeDb()
    }

    fun deleteFromDb(id: String, image: String){
        openDb()
        deleteImageFromScopeStorage(image)
        db.delete(MyDbNameClass.TABLE_NAME,"_id=${id}",null)
        closeDb()
    }

    fun getAllCards(): ArrayList<CardData> {
        openDb()
        val cards = ArrayList<CardData>()
        val cursor: Cursor = db.rawQuery(MyDbNameClass.SELECT_ALL, null)
        with(cursor) {
            if (moveToFirst()) {
                do {
                    val text = getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TEXT))
                    val image = getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_IMAGE))
                    val imageMipmap = getBlob(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_IMAGE_MIPMAP))
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                    val data = getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_DATA))
                    val card = CardData(image, text, id, data, imageMipmap)
                    cards.add(card)
                } while (moveToNext())
            }
            close()
        }

        closeDb()
        return cards
    }
    private fun closeDb():Unit = myDbHelper.close()

    private fun deleteImageFromScopeStorage(imageUri: String) {
        val imageFile = File(imageUri.toUri().path!!)
        imageFile.canonicalFile.delete()
    }
}