package com.example.modul9

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class InsertNoteActivity : AppCompatActivity(), View.OnClickListener {
    private var tvEmail: TextView? = null
    private var tvUid: TextView? = null
    private lateinit var btnKeluar: Button
    private var mAuth: FirebaseAuth? = null
    private var etTitle: EditText? = null
    private var etDesc: EditText? = null
    private lateinit var btnSubmit: Button
    private lateinit var db: DatabaseReference
    private lateinit var notedb: DatabaseReference
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private lateinit var rv: RecyclerView
    private var book: MutableList<Note> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_note)
        tvEmail = findViewById(R.id.tv_email)
        tvUid = findViewById(R.id.tv_uid)
        btnKeluar = findViewById(R.id.btn_keluar)
        mAuth = FirebaseAuth.getInstance()
        btnKeluar.setOnClickListener(this)
        etTitle = findViewById(R.id.et_title)
        etDesc = findViewById(R.id.et_description)
        btnSubmit = findViewById(R.id.btn_submit)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.reference
        btnSubmit.setOnClickListener(this)

        rv = findViewById(R.id.rv)
        val adapter = BookAdapter(this,book)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser!!.uid
        db = FirebaseDatabase.getInstance("https://modul9-4e60c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
        notedb = db.child(userId).child("notes")

        notedb.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                book.clear()
                for(booksnapshot in snapshot.children){
                    val note = booksnapshot.getValue(Note::class.java)
                    note?.id = booksnapshot.key
                    if (note !== null){
                        book.add(note)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@InsertNoteActivity, "Tidak bisa mendapatkan data", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onStart() {

        super.onStart()
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            tvEmail!!.text = currentUser.email
            tvUid!!.text = currentUser.uid
        }
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_keluar -> logOut()
            R.id.btn_submit -> submitData()
        }
    }
    fun logOut() {
        mAuth!!.signOut()
        val intent = Intent(this@InsertNoteActivity,
            MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    fun submitData() {
        if (!validateForm()) {
            return
        }
        val title = etTitle!!.text.toString()
        val desc = etDesc!!.text.toString()
        val id = notedb.push().key
        val baru = Note(id = id
            ,title = title
            , description  = desc)
        notedb.child(id!!).setValue(baru).addOnSuccessListener {
            Toast.makeText(this@InsertNoteActivity, "Add data",
                Toast.LENGTH_SHORT).show() }.addOnFailureListener(this)
        {
            Toast.makeText(this@InsertNoteActivity, "Failed to Add data",
                Toast.LENGTH_SHORT).show() }
        etTitle!!.text.clear()
        etDesc!!.text.clear()
    }
    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(etTitle!!.text.toString())) {
            etTitle!!.error = "Required"
            result = false
        } else {
            etTitle!!.error = null
        }
        if (TextUtils.isEmpty(etDesc!!.text.toString())) {
            etDesc!!.error = "Required"
            result = false
        } else {
            etDesc!!.error = null
        }
        return result
    }

}