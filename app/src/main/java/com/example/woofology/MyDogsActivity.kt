package com.example.woofology

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woofology.Database.DBManager
import com.example.woofology.ModelClasses.Dog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList

class MyDogsActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private val GALLERY_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private lateinit var addDogFab: FloatingActionButton
    private var newDogPhotoPath: String? = null
    private lateinit var addDogText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var dogsRVList: LinkedList<Dog>
    private lateinit var mAdapter: DogRVListAdapter
    private lateinit var coordinatorLayout: LinearLayout
    private lateinit var getPicture: ActivityResultLauncher<Intent>
    private lateinit var bottomNavigationView: BottomNavigationView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_dogs)

        addDogFab = findViewById(R.id.add_dog)
        addDogText = findViewById(R.id.addDogs)
        addDogFab.setOnTouchListener(object : View.OnTouchListener {
            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    addDogFab.backgroundTintList =
                        ContextCompat.getColorStateList(this@MyDogsActivity, R.color.redLight)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    addDogFab.backgroundTintList =
                        ContextCompat.getColorStateList(this@MyDogsActivity, R.color.red)
                    showPopUpMediaOptions()
                }
                return true
            }
        })

        recyclerView = findViewById(R.id.recycler_view)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        bottomNavigationView = findViewById(R.id.bottom_nav_view)

        val itemTouchHelperCallback =
            RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        dogsRVLoadData()

        getPicture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = Intent(this@MyDogsActivity, DogAnalysisActivity::class.java)
                val b = Bundle()
                b.putString("imageUri", newDogPhotoPath)
                intent.putExtras(b)
                startActivity(intent)
            }
        }

        bottomNavigationView.selectedItemId = R.id.find_dog_activity
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_dog_activity -> return@setOnNavigationItemSelectedListener true
                R.id.list_activity -> {

                    Intent(this@MyDogsActivity, AllBreeds::class.java).also {
                        startActivity(it)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.random_activity -> {

                // Todo: Make RandomDogsPictureActivity

                }
                R.id.quiz_activity -> {

                // Todo: Make DogQuiz Activity

                }
                R.id.downloaded_images_activity -> {

                    Intent(this@MyDogsActivity, Downloads::class.java).also {
                        startActivity(it)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    return@setOnNavigationItemSelectedListener true

                }
            }
            false
        }

    }

    private fun showPopUpMediaOptions() {
        val openDialog = Dialog(this@MyDogsActivity)
        openDialog.setContentView(R.layout.select_camera_or_gallery_dialog)
        val camera = openDialog.findViewById<TextView>(R.id.cameraBtn)
        val gallery = openDialog.findViewById<TextView>(R.id.galleryBtn)

        gallery.setOnClickListener {
            loadImagefromGallery()
            openDialog.dismiss()
        }

        camera.setOnClickListener {
            callCameraPermissionAndValidation()
            openDialog.dismiss()
        }

        openDialog.show()
    }

    private fun loadImagefromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY_REQUEST)
    }

    private fun takePicture() {
        val takePictureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Log.e("Fail create image", ex.toString())
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.woofology.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                getPicture.launch(takePictureIntent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data == null) {
                return
            }
            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != data) {
                val selectedImage: Uri = data.data!!
                newDogPhotoPath = copyFileFromGallery(selectedImage)
            }
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {

            }
            val intent = Intent(this@MyDogsActivity, DogAnalysisActivity::class.java)
            val b = Bundle()
            b.putString("imageUri", newDogPhotoPath) //Your id
            intent.putExtras(b)
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        }
    }


    private fun callCameraPermissionAndValidation() {
        if (checkCallingOrSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture()
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                val messageCamera = "Camera permission is needed"
                Toast.makeText(this@MyDogsActivity, messageCamera, Toast.LENGTH_SHORT).show()
            }
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                val messagePermissions = "Permission was not granted"
                Toast.makeText(this@MyDogsActivity, messagePermissions, Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,    //prefix
            ".jpg",    //suffix
            storageDir      // directory
        )
        newDogPhotoPath = image.absolutePath
        return image
    }

    private fun copyFileFromGallery(uriImageGallery: Uri): String {
        val helperImgView = ImageView(this)
        helperImgView.setImageURI(uriImageGallery)
        val path = uriImageGallery.path
        val bitmap = (helperImgView.drawable as BitmapDrawable).bitmap
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,     // prefix
            ".jpg",     // suffix
            storageDir       // directory
        )
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()
        val fos = FileOutputStream(image)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return image.absolutePath
    }

    override fun onResume() {
        super.onResume()
        dogsRVLoadData()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is DogRVListAdapter.MyViewHolder) {

            val builder = AlertDialog.Builder(this)

            builder.setTitle("Confirmation")
            builder.setMessage("Do you permanently want to delete this? ")

            builder.setPositiveButton("Yes") { dialog, which ->

                val deletedDog = dogsRVList[viewHolder.adapterPosition]
                val deletedIndex = viewHolder.adapterPosition
                deleteDog(deletedDog, deletedIndex)
                mAdapter.removeItem(viewHolder.adapterPosition)

            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()

            dialog.setCanceledOnTouchOutside(false)
            dialog.show()



        }
    }

    private fun dogsRVLoadData() {
        val dbManager = DBManager(this, this)
        dbManager.open()
        dogsRVList = dbManager.getDogs()
        dbManager.close()
        mAdapter = DogRVListAdapter(this, this, dogsRVList)
        recyclerView.adapter = mAdapter
        removeAddDogsMessage()
    }

    private fun deleteDog(deletedDog: Dog, position: Int) {
        val dbManager = DBManager(this, this)
        dbManager.open()
        dbManager.deleteDog(deletedDog.id)
        dbManager.close()
        val filePath = deletedDog.uriImage
        val file = File(filePath)
        file.delete()
    }

     fun removeAddDogsMessage() {
        if (dogsRVList.size == 0) {
            addDogText.visibility = View.VISIBLE
        } else {
            addDogText.visibility = View.INVISIBLE
        }
    }

}