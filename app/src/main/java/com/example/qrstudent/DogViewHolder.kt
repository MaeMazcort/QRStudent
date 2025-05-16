package com.example.qrstudent

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

// ViewHolder para el RecyclerView
class DogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imgDog: ImageView = view.findViewById(R.id.imgDog)

    fun bind(imageUrl: String) {
        Picasso.get().load(imageUrl).into(imgDog)  // Cargar imagen desde la API
    }
}
