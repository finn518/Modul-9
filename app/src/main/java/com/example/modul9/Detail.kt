package com.example.modul9

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Detail : AppCompatActivity() {

    private var noteId: String? = null
    private lateinit var notedb: DatabaseReference
    private lateinit var detailTitle: TextView
    private lateinit var detailDesc: TextView
    private lateinit var update: Button
    private lateinit var updateForm: LinearLayout
    private lateinit var newTitle: EditText
    private lateinit var newDesc: EditText
    private lateinit var btnUpdateNow: Button
    private lateinit var noteImage: ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val bun: Bundle? = intent.extras
        detailTitle = findViewById(R.id.detailTitle)
        detailDesc = findViewById(R.id.detailDesc)
        update = findViewById(R.id.btn_update)
        val keluar: Button = findViewById(R.id.btn_out)
        updateForm = findViewById(R.id.lin_upt)
        newTitle = findViewById(R.id.titleUpdate)
        newDesc = findViewById(R.id.descUpdate)
        btnUpdateNow = findViewById(R.id.updateNow)
        noteImage = findViewById(R.id.noteImg)


        val title = bun!!.getString("title")
        val desc = bun.getString("description")
        noteId = bun.getString("id")

        detailTitle.text = title
        detailDesc.text = desc

        notedb = FirebaseDatabase.getInstance("https://modul9-4e60c-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("notes").child(noteId!!)

        update.setOnClickListener {
            updateForm.visibility = View.VISIBLE
            detailTitle.visibility = View.GONE
            noteImage.visibility = View.GONE
            detailDesc.visibility = View.GONE
            newTitle.setText(detailTitle.text)
            newDesc.setText(detailDesc.text)
        }

        btnUpdateNow.setOnClickListener {
            updateNote()
        }

        keluar.setOnClickListener {
            finish()
        }


    }

    private fun updateNote() {
        val updatedTitle = newTitle.text.toString()
        val updatedDesc = newDesc.text.toString()

        if (updatedTitle.isEmpty() || updatedDesc.isEmpty()) {
            Toast.makeText(this, "Harus di isi", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val note = mapOf(
                "title" to updatedTitle,
                "description" to updatedDesc
            )
            notedb.updateChildren(note).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Detail, "Berhasil update", Toast.LENGTH_SHORT).show()
                updateForm.visibility = View.GONE
                detailTitle.visibility = View.VISIBLE
                detailDesc.visibility = View.VISIBLE
                noteImage.visibility = View.VISIBLE
                detailTitle.text = updatedTitle
                detailDesc.text = updatedDesc
            }
        }
    }
}