package com.example.woofology

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

class BreedImages : AppCompatActivity(), onItemClick {

    private lateinit var breedImages: RecyclerView
    private lateinit var adapter: BreedImageAdapter
    private lateinit var client: ApiClient
    private lateinit var type: String
    var images: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breed_images)


        breedImages = findViewById(R.id.rv_breed_images)

        type = intent.getStringExtra("name")!!

        // ActionBar
        supportActionBar?.apply {
            title = type.toUpperCase()
            // set back button in action bar
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        client = ApiClient(this)
        client.getBreedImages(listener, type)

    }

    private val listener = object : AllBreedsListener {
        override fun onFetch(response: AllBreedsResponse?, message: String?) {
            if (response?.message?.isEmpty() == true) {
                Toast.makeText(this@BreedImages, "No Dog Breeds Available", Toast.LENGTH_SHORT).show()
                return
            }
            if (response != null) {
                showAllBreeds(response)
            }
        }

        override fun onError(message: String?) {
            Toast.makeText(this@BreedImages, "Error: $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAllBreeds(response: AllBreedsResponse) {
        breedImages.setHasFixedSize(true)
        breedImages.layoutManager = GridLayoutManager(this, 4)
        adapter = BreedImageAdapter(this, response.message!! as MutableList<String>, this)
        breedImages.adapter = adapter

        images = (response.message as MutableList<String>?)!!
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onImgItemClick(link: String?, position: Int) {
        val intent = Intent(this@BreedImages, FullImageActivity::class.java).apply {
            putExtra("list", images as Serializable)
            putExtra("position", position)
            putExtra("activity", "breeds")
        }
        startActivity(intent)
    }

}