package com.example.mynotesapp.helper

import android.database.Cursor
import com.example.mynotesapp.entity.Note
import com.example.mynotesapp.db.DatabaseContract

object MappingHelper {
    fun mapCursorToArrayList(noteCursor: Cursor?) : ArrayList<Note>{
        val noteList = ArrayList<Note>()

        noteCursor?.apply {
            while (moveToNext()){
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                noteList.add(Note(id, title, description, date))
            }
        }
        return noteList
    }

    fun mapCursorToObject(noteCursor: Cursor?): Note {
        var note = Note()
        noteCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
            val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
            val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
            note = Note(id, title, description, date)
        }
        return note
    }
}