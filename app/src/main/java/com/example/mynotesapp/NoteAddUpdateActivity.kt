package com.example.mynotesapp

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mynotesapp.databinding.ActivityNoteAddUpdateBinding
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.DATE
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.example.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TITLE
import com.example.mynotesapp.db.NoteHelper
import com.example.mynotesapp.entity.Note
import com.example.mynotesapp.helper.MappingHelper
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var noteHelper: NoteHelper
    private lateinit var uriWithId: Uri

    private lateinit var binding: ActivityNoteAddUpdateBinding

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null){
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit  = true
        } else {
            note = Note()
        }

        var actionbarTitle: String
        var btnTitle: String

        if (isEdit){

            // Uri yang didapatkan disini akan digunakan untuk ambil data dari provider
//                content://com.example.mynotesapp//note/id

            uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + note?.id)

            val cursor = contentResolver.query(uriWithId, null, null, null, null)
            if (cursor != null){
                note = MappingHelper.mapCursorToObject(cursor)
                cursor.close()
            }

            actionbarTitle = "Ubah"
            btnTitle = "Update"

            note?.let {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }
        } else {
            actionbarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionbarTitle

        binding.btnSubmit.text = btnTitle

        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_submit){
            val title = binding.edtTitle.text.toString().trim()
            val description = binding.edtDescription.text.toString().trim()

            // jika field nya masih kosong maka tampilkan error
            if (title.isEmpty()){
                binding.edtTitle.error = "Field can not be blank"
                return
            }

            // Gunakan contentvalues untuk menampung data
            val values = ContentValues()
            values.put(TITLE, title)
            values.put(DESCRIPTION, description)

//            Jika merupakan edit setresultnya UPDATE, dan jika bukan maka
//            setresultnya ADD

            if (isEdit){
                // Gunakan uriWithId dari intent activity ini
                // content://com.example.mynotesapp/note/id
                contentResolver.update(uriWithId, values, null, null)
                Toast.makeText(this, "Satu item berhasil diedit", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                values.put(DATE, getCurrentDate())
                // Gunakan content uri untuk insert
                // content://com.example.mynotesapp/note
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this, "Satu item berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit){
            menuInflater.inflate(R.menu.menu_form, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int){
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose){
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Note"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya"){ _, _ ->
                if (isDialogClose){
                    finish()
                } else {
                    // Gunakan uriWithId untuk delete
                    // content://com.example.mynotesapp/note/id
                    contentResolver.delete(uriWithId, null, null)
                    Toast.makeText(this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Tidak"){ dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}