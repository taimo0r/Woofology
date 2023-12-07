package com.example.woofology

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import java.util.Random



class RandomDog : AppCompatActivity() {

    private lateinit var imgRandom: ImageView
    private lateinit var nameRandom: TextView
    private lateinit var nextRandom: Button
    private lateinit var downloadBtn: FloatingActionButton
    private lateinit var setWallpaper: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var client: ApiClient
    private lateinit var imgLink: String
    private lateinit var name: String
    private val random = Random()
    private var key: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_dog)

        //ActionBar
        val actionBar = supportActionBar
        actionBar?.hide()
        imgRandom = findViewById<ImageView>(R.id.iv_random)
        nameRandom = findViewById<TextView>(R.id.tv_breed)
        nextRandom = findViewById<Button>(R.id.btn_random)
        downloadBtn = findViewById(R.id.fab_download)
        setWallpaper = findViewById(R.id.fab_wallpaper)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        key = random.nextInt(9999 - 1000) + 1000
        client = ApiClient(this)
        client.getRandomBreed(listener)
        bottomNavigationView.setSelectedItemId(R.id.random_activity)
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_dog_activity -> {
                    val analysisIntent = Intent(this@RandomDog, MyDogsActivity::class.java)
                    startActivity(analysisIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.list_activity -> {
                    val allBreedsIntent = Intent(this@RandomDog, AllBreeds::class.java)
                    startActivity(allBreedsIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.random_activity -> return@OnNavigationItemSelectedListener true
                R.id.quiz_activity -> {



                }

                R.id.downloaded_images_activity -> {
                    val downloadsIntent = Intent(this@RandomDog, Downloads::class.java)
                    startActivity(downloadsIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        nextRandom.setOnClickListener(View.OnClickListener { client.getRandomBreed(listener) })
        setWallpaper.setOnClickListener(View.OnClickListener {
            val wallpaperManager = WallpaperManager.getInstance(this@RandomDog)
            val bitmap = (imgRandom.getDrawable() as BitmapDrawable).bitmap
            try {
                wallpaperManager.setBitmap(bitmap)
                Toast.makeText(this@RandomDog, "Wallpaper Set!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RandomDog, "Wallpaper cannot be set", Toast.LENGTH_SHORT).show()
            }
        })
        downloadBtn.setOnClickListener(View.OnClickListener { downloadImage() })
    }

    private val listener: RandomBreedListener = object : RandomBreedListener {
        override fun onFetch(response: RandomBreedResponse?, message: String?) {
            if (response?.message?.isEmpty() == true) {
                Toast.makeText(this@RandomDog, "No Dog Breeds Available", Toast.LENGTH_SHORT).show()
                return
            }
            if (response != null) {
                showRandomBreeds(response)
            }
        }

        override fun onError(message: String?) {

        }

    }

    private fun showRandomBreeds(response: RandomBreedResponse) {
        imgLink = response.message.toString()
        Picasso.get().load(imgLink).into(imgRandom)
        name = Common.getBreedFromLink(response.message.toString())
        nameRandom.text = name
    }

    private fun downloadImage() {
        var downloadManager: DownloadManager? = null
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(imgLink)
        val finalName = name + "_" + key
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle("DogBreeds_$key")
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                "DogBreeds/$finalName.jpg"
            )
        downloadManager.enqueue(request)
        Toast.makeText(this@RandomDog, "Downloading Started!", Toast.LENGTH_SHORT).show()
    }
}