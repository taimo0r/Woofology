package com.example.woofology

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllBreeds : AppCompatActivity() {

    private lateinit var allBreeds: RecyclerView
    private lateinit var adapter: AllBreedsAdapter
    private lateinit var client: ApiClient
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_breeds)

        // ActionBar
        supportActionBar?.apply {
            title = "All Dog Breeds"
            // set back button in action bar
        }

        allBreeds = findViewById(R.id.rv_dog_breeds)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        client = ApiClient(this)
        client.getAllBreeds(listener)

        bottomNavigationView.selectedItemId = R.id.list_activity
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.find_dog_activity -> {

                    Intent(this@AllBreeds, MyDogsActivity::class.java).also {
                        startActivity(it)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.list_activity -> return@setOnNavigationItemSelectedListener true

                R.id.random_activity -> {
                //Todo: Make RandomDogActivity
                }
                R.id.quiz_activity -> {
                //Todo: Make QuizActivity
                }
                R.id.downloaded_images_activity -> {
                //Todo: Make DownloadedImagedActivity
                }
            }
            false
        }

    }

    private val listener = object : AllBreedsListener {


        override fun onFetch(response: AllBreedsResponse?, message: String?) {
            if (response?.message?.isEmpty() == true) {
                Toast.makeText(this@AllBreeds, "No Dog Breeds Available", Toast.LENGTH_SHORT).show()
                return
            }
            if (response != null) {
                showAllBreeds(response)
            }
        }

        override fun onError(message: String?) {
            Toast.makeText(this@AllBreeds, "Error: $message", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showAllBreeds(response: AllBreedsResponse) {
        allBreeds.setHasFixedSize(true)
        allBreeds.layoutManager = GridLayoutManager(this, 2)
        adapter = AllBreedsAdapter(this, response.message as MutableList<String>)
        allBreeds.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}