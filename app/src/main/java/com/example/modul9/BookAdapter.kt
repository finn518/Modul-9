package com.example.modul9

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookAdapter (private val context: Context, private val data:MutableList<Note>): RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.itemTitle)
        val delBtn: ImageButton = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rvitems, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val current = data[position]
        holder.delBtn.setOnClickListener(){
            val itemPosition = holder.adapterPosition
            if(itemPosition != RecyclerView.NO_POSITION){
                val deleteNote = data[position]
                CoroutineScope(Dispatchers.IO).launch{
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser!!.uid
                    val databaseRef = FirebaseDatabase.getInstance("https://modul9-4e60c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
                        .child(userId).child("notes")
                    databaseRef.child(deleteNote.id!!).removeValue().await()
                    withContext(Dispatchers.Main){
                        notifyItemRangeChanged(position,data.size)
                    }

                }
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Detail::class.java)
            intent.putExtra("id", current.id)
            intent.putExtra("title", current.title)
            intent.putExtra("description", current.description)
            context.startActivity(intent)
        }
        holder.title.text = current.title
    }

    override fun getItemCount(): Int {
        return  data.size
    }
}

