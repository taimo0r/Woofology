package com.example.woofology

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.woofology.Database.DBManager
import com.example.woofology.ModelClasses.Dog
import com.example.woofology.ModelClasses.DogData

class UpdateDogActivity : AppCompatActivity() {
    var dogPhoto: ImageView? = null
    var breedOne: TextView? = null
    var breedTwo: TextView? = null
    var breedThree: TextView? = null
    var height: TextView? = null
    var weight: TextView? = null
    var origin: TextView? = null
    var lifeSpan: TextView? = null
    var temperament: TextView? = null
    var share: ImageButton? = null
    var dogHealthIssues: LinearLayout? = null
    var idDog = 0
    var dog: Dog? = null
    var dogDataSelected: DogData? = null
    var dogDataBreedOne: DogData? = null
    var dogDataBreedTwo: DogData? = null
    var dogDataBreedThree: DogData? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_analysis)
        dogPhoto = findViewById(R.id.dogPhoto)
        breedOne = findViewById(R.id.breedOne)
        breedTwo = findViewById(R.id.breedTwo)
        breedThree = findViewById(R.id.breedThree)
        height = findViewById(R.id.height)
        weight = findViewById(R.id.weight)
        origin = findViewById(R.id.origin)
        lifeSpan = findViewById(R.id.lifeSpan)
        temperament = findViewById(R.id.temperament)
        dogHealthIssues = findViewById(R.id.dogHealthIssues)
        share = findViewById(R.id.shareButton)
        setDogData()
        dogsBreedsListeners()
    }

    private fun setDogData() {
        val bundle = intent.extras
        idDog = bundle!!.getInt("idDog")
        val dbManager = DBManager(this, this)
        dbManager.open()
        dog = dbManager.getDog(idDog)
        dogDataSelected = dbManager.getDogData(dog!!.selectedBreed)
        dogDataBreedOne = dbManager.getDogData(dog!!.breedOneId)
        dogDataBreedTwo = dbManager.getDogData(dog!!.breedTwoId)
        dogDataBreedThree = dbManager.getDogData(dog!!.breedThreeId)
        dbManager.close()
        val imageCameraBitmap = BitmapFactory.decodeFile(dog!!.uriImage)
        dogPhoto!!.setImageBitmap(imageCameraBitmap)
        breedOne!!.text = dogDataBreedOne!!.name + ": " + dog!!.percentageBreedOne + " %"
        breedTwo!!.text = dogDataBreedTwo!!.name + ": " + dog!!.percentageBreedTwo + " %"
        breedThree!!.text = dogDataBreedThree!!.name + ": " + dog!!.percentageBreedThree + " %"
        height!!.text = "Height: " + dogDataSelected!!.height
        weight!!.text = "Weight: " + dogDataSelected!!.weight
        origin!!.text = "Origin: " + dogDataSelected!!.origin
        lifeSpan!!.text = "Life Span: " + dogDataSelected!!.lifeSpan
        temperament!!.text = dogDataSelected!!.temperament

        getHealthIssues(dogDataSelected!!.health, dogHealthIssues)
        if (dog!!.selectedBreed == dog!!.breedOneId) {
            breedOne!!.setTextColor(resources.getColor(R.color.blue_dockdeck))
            breedTwo!!.setTextColor(resources.getColor(R.color.black))
            breedThree!!.setTextColor(resources.getColor(R.color.black))
        }
        if (dog!!.selectedBreed == dog!!.breedTwoId) {
            breedOne!!.setTextColor(resources.getColor(R.color.black))
            breedTwo!!.setTextColor(resources.getColor(R.color.blue_dockdeck))
            breedThree!!.setTextColor(resources.getColor(R.color.black))
        }
        if (dog!!.selectedBreed == dog!!.breedThreeId) {
            breedOne!!.setTextColor(resources.getColor(R.color.black))
            breedTwo!!.setTextColor(resources.getColor(R.color.black))
            breedThree!!.setTextColor(resources.getColor(R.color.blue_dockdeck))
        }
    }

    private fun dogsBreedsListeners() {
        breedOne!!.setOnClickListener {
            dog!!.selectedBreed = dog!!.breedOneId
            updateSelectedBreed(idDog, dog!!.breedOneId)
            setDogData()
        }
        breedTwo!!.setOnClickListener {
            dog!!.selectedBreed = dog!!.breedTwoId
            updateSelectedBreed(idDog, dog!!.breedTwoId)
            setDogData()
        }
        breedThree!!.setOnClickListener {
            dog!!.selectedBreed = dog!!.breedThreeId
            updateSelectedBreed(idDog, dog!!.breedThreeId)
            setDogData()
        }
        share!!.setOnClickListener {
            ShareDogData.shareDogInfo(this@UpdateDogActivity)
        }
    }

    private fun updateSelectedBreed(dogId: Int, selectedBreedId: Int) {
        val dbManager = DBManager(this, this)
        dbManager.open()
        dbManager.updateDog(dogId, selectedBreedId)
        dbManager.close()
    }

    private fun getHealthIssues(health: String, linearLayout: LinearLayout?) {
        linearLayout!!.removeAllViews()
        val arrOfStr = health.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in 1 until arrOfStr.size) {
            val lInflater = LayoutInflater.from(this)
            val view = lInflater.inflate(R.layout.dog_health_issue, null)
            val issue = view.findViewById<TextView>(R.id.dogIssue)
            issue.text = arrOfStr[i]
            linearLayout.addView(issue)
        }
    }
}