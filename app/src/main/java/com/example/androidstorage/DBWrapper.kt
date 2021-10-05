package com.example.androidstorage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBWrapper(context: Context) {

    val helper : DBHelper = DBHelper(context)
    val db : SQLiteDatabase = helper.writableDatabase     // we made database as readable

    fun addStudent(std : Student): Long{
        // Insertion into the table
        val rowData = ContentValues()
        rowData.put(DBHelper.CLM_STD_ID, std.id)
        rowData.put(DBHelper.CLM_STD_NAME, std.name)
        rowData.put(DBHelper.CLM_STD_MARKS, std.marks)
        return db.insert(DBHelper.TABLE_NAME,null,rowData)
    }

    fun getStudent(): Cursor{
        // Select Query
        // we will retrieve data from this function
        val clms = arrayOf(DBHelper.CLM_STD_ID, DBHelper.CLM_STD_NAME, DBHelper.CLM_STD_MARKS)
        return db.query(DBHelper.TABLE_NAME,clms,null,null,null,null,"${DBHelper.CLM_STD_MARKS} desc")   // default is ascending
    }

    fun editStudent(std: Student) : Int{
        // update info
        val rowData = ContentValues()
        rowData.put(DBHelper.CLM_STD_ID, std.id)
        rowData.put(DBHelper.CLM_STD_NAME, std.name)
        rowData.put(DBHelper.CLM_STD_MARKS, std.marks)
        var args = arrayOf("${std.id}")
        return db.update(DBHelper.TABLE_NAME, rowData,"${DBHelper.CLM_STD_ID} = ?", args )   // at runtime "?" will be replaced by given ID
    }

    fun deleteStudent(std: Student) : Int{
        // delete student from database table
        var args = arrayOf("${std.id}")
        return db.delete(DBHelper.TABLE_NAME, "${DBHelper.CLM_STD_ID} = ?", args )
    }


}