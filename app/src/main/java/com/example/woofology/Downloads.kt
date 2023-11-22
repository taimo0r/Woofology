package com.example.woofology


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class Downloads : AppCompatActivity(), onItemClick {

    private var imgFiles: ArrayList<DownloadedPictures> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: downloadedImgAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var noDownloadsText: TextView

    companion object {
        const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloads)

        // ActionBar
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.downloaded_images)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        noDownloadsText = findViewById(R.id.no_downloaded_imgs)

        runtimePermission()

        bottomNavigationView.setSelectedItemId(R.id.downloaded_images_activity)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_dog_activity -> {
                    val analysisIntent = Intent(this@Downloads, MyDogsActivity::class.java)
                    startActivity(analysisIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.list_activity -> {
                    val allBreedsIntent = Intent(this@Downloads, AllBreeds::class.java)
                    startActivity(allBreedsIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.random_activity -> {
                    //Todo: Make RandomDogsActivity
                }
                R.id.quiz_activity -> {
                    //Todo: Make QuizActivity
                }
                R.id.downloaded_images_activity -> return@setOnNavigationItemSelectedListener true
            }
            false
        }

    }


    private fun runtimePermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        } else {
            imgFiles = getImages(this)
            initAdapter()
        }
    }

    private fun initAdapter() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = downloadedImgAdapter(this, imgFiles, this)
        recyclerView.adapter = adapter
        removeNoDownloadsText()
    }

    private fun getImages(context: Context): ArrayList<DownloadedPictures> {
        val tempImgList = arrayListOf<DownloadedPictures>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?"
        val argsArray = arrayOf("DogBreeds")

        context.contentResolver.query(uri, projection, selection, argsArray, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val title = cursor.getString(0)
                val path = cursor.getString(1)

                val imgFile = DownloadedPictures(title, path)
                Log.e("Path:", "$path$title")
                tempImgList.add(imgFile)
            }
        }

        return tempImgList
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imgFiles = getImages(this)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }



    override fun onImgItemClick(link: String?, position: Int) {
        val intent = Intent(this, FullImageActivity::class.java)
        intent.putExtra("link", link)
        intent.putExtra("position", position)
        intent.putExtra("activity", "downloads")
        startActivity(intent)
    }

    fun removeNoDownloadsText() {
        if (imgFiles.size == 0) {
            noDownloadsText.visibility = View.VISIBLE
        } else {
            noDownloadsText.visibility = View.INVISIBLE
        }
    }
}
