package com.example.woofology

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woofology.Database.DBManager
import com.example.woofology.ModelClasses.Dog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.LinkedList

class MyDogsActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{


    private lateinit var addDogFab: FloatingActionButton
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
             // Todo: Make DogAnalysisActivity
            }
        }

        bottomNavigationView.selectedItemId = R.id.find_dog_activity
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_dog_activity -> return@setOnNavigationItemSelectedListener true

                R.id.list_activity -> {

                //Todo: Make AllBreedsActivity

                }
                R.id.random_activity -> {

                // Todo: Make RandomDogsPictureActivity

                }
                R.id.quiz_activity -> {

                // Todo: Make DogQuiz Activity

                }
                R.id.downloaded_images_activity -> {

                // Todo: Make DownloadedImagesActivity

                }
            }
            false
        }

    }



    override fun onResume() {
        super.onResume()
        dogsRVLoadData()
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



     fun removeAddDogsMessage() {
        if (dogsRVList.size == 0) {
            addDogText.visibility = View.VISIBLE
        } else {
            addDogText.visibility = View.INVISIBLE
        }
    }

}