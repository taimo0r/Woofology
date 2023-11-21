package com.example.woofology

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.util.*

class FullImageActivity : AppCompatActivity() {

    private lateinit var dogImage: ImageView
    private var dogName: String? = null
    private var link: String? = null
    private var activity = ""
    private var list: List<String>? = null
    private var position: Int = 0
    private lateinit var nextBtn: Button
    private lateinit var prevBtn: Button
    private lateinit var downloadBtn: FloatingActionButton
    private lateinit var setWallpaper: FloatingActionButton
    private val random = Random()
    private var key: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        dogImage = findViewById(R.id.dog_image)
        downloadBtn = findViewById(R.id.fab_download)
        setWallpaper = findViewById(R.id.fab_wallpaper)
        nextBtn = findViewById(R.id.next_btn)
        prevBtn = findViewById(R.id.prev_btn)

        activity = intent.getStringExtra("activity") ?: ""

        if (activity == "downloads") {
            link = intent.getStringExtra("link")
            nextBtn.visibility = View.GONE
            prevBtn.visibility = View.GONE
            setWallpaper.visibility = View.GONE
            downloadBtn.visibility = View.GONE

            val bitmap = BitmapFactory.decodeFile(link)
            dogImage.setImageBitmap(bitmap)
        } else {
            list = intent.getSerializableExtra("list") as List<String>?
            position = intent.getIntExtra("position", -1)
        }

        key = random.nextInt(9999 - 1000) + 1000

        list?.let {
            Picasso.get().load(it[position]).into(dogImage)
            dogName = Common.getBreedFromLink(it[position])
        }

        setWallpaper.setOnClickListener {
            val wallpaperManager = WallpaperManager.getInstance(this)
            val bitmap = dogImage.drawable.toBitmap()

            try {
                wallpaperManager.setBitmap(bitmap)
                Toast.makeText(this, "Wallpaper Set!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Wallpaper cannot be set", Toast.LENGTH_SHORT).show()
            }
        }

        downloadBtn.setOnClickListener {
            downloadImage()
        }

        nextBtn.setOnClickListener {
            list?.let {
                if (position < it.size) {
                    position++
                    Picasso.get().load(it[position]).into(dogImage)
                    dogName = Common.getBreedFromLink(it[position])
                }
            }
        }

        prevBtn.setOnClickListener {
            if (position > 0) {
                position--
                list?.let {
                    Picasso.get().load(it[position]).into(dogImage)
                    dogName = Common.getBreedFromLink(it[position])
                }
            }
        }
    }

    private fun downloadImage() {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(list?.get(position))
        val finalName = "${dogName}_$key"

        val request = DownloadManager.Request(uri)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle("DogBreeds_${dogName}_$key")
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "DogBreeds/$finalName.jpg")

        downloadManager.enqueue(request)
        Toast.makeText(this, "Downloading Started!", Toast.LENGTH_SHORT).show()
    }
}