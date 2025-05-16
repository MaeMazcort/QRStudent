package com.example.qrstudent

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class DogActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DogAdapter
    private val images = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog)  // Usa el layout correcto

        recycler = findViewById(R.id.rvDogs)
        adapter = DogAdapter(images)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.svDogs)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    buscarPorRaza(it.lowercase())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

    private fun buscarPorRaza(raza: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(APIServices::class.java)

        service.getDogsByBreeds(raza).enqueue(object : Callback<DogsResponse> {
            override fun onResponse(call: Call<DogsResponse>, response: Response<DogsResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val perros = response.body()?.images ?: emptyList()
                    images.clear()
                    images.addAll(perros)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@DogActivity, "Raza no encontrada", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DogsResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@DogActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
