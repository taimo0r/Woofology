package com.example.woofology

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.woofology.Database.DBManager
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Collections
import java.util.LinkedList


class DogAnalysisActivity : AppCompatActivity() {


    private val handler = Handler()
    private lateinit var progress: ProgressDialog
    private lateinit var dogPhoto: ImageView
    private lateinit var breedOne: TextView
    private lateinit var breedTwo: TextView
    private lateinit var breedThree: TextView
    private lateinit var height: TextView
    private lateinit var weight: TextView
    private lateinit var origin: TextView
    private lateinit var lifeSpan: TextView
    private lateinit var temperament: TextView
    private lateinit var share: ImageButton
    private lateinit var dogHealthIssues: LinearLayout
    private var uri = ""
    private var strBreedOne = ""
    private var strBreedTwo = ""
    private var strBreedThree = ""
    private var percentageBreedOne = 0f
    private var percentageBreedTwo = 0f
    private var percentageBreedThree = 0f
    private var selectedBreed = 0
    private var idDogCreated = 0
    private val labels = ArrayList<String>()
    private var resultsMap = HashMap<String, Float>()
    private val mapBreedToIndex = HashMap<String, Int>()


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
        breedOne.setTextColor(resources.getColor(R.color.blue_dockdeck))

        progress = ProgressDialog(this)
        progress.setTitle("Analyzing Image")
        progress.setMessage("Wait while loading...")
        progress.setCancelable(false)
        progress.show()

        setDogPhoto()
        dogsBreedsListeners()

        handler.postDelayed({
            analyzeImage()
        }, 1500)

    }


    private fun setDogPhoto() {
        val bundle = intent.extras
        uri = bundle?.getString("imageUri") ?: ""
        val imageCameraBitmap = BitmapFactory.decodeFile(uri)
        dogPhoto.setImageBitmap(imageCameraBitmap)
    }

    private fun loadLabels(): ArrayList<String> {
        val labelList = ArrayList<String>()
        try {
            val reader = BufferedReader(InputStreamReader(assets.open("labels.txt")))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                labelList.add(line!!)
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return labelList
    }

    private fun sortByValue(hm: HashMap<String, Float>): HashMap<String, Float> {
        val list: List<Map.Entry<String, Float>> = LinkedList(hm.entries)

        Collections.sort(list) { o1, o2 -> o1.value.compareTo(o2.value) }

        Collections.reverse(list)
        val temp: HashMap<String, Float> = LinkedHashMap()
        for (aa in list) {
            temp[aa.key] = aa.value
        }
        return temp
    }

    private fun saveResults() {
        val dbManager = DBManager(this, this)
        dbManager.open()
        val fkBreedOne = mapBreedToIndex[strBreedOne] ?: 0
        val fkBreedTwo = mapBreedToIndex[strBreedTwo] ?: 0
        val fkBreedThree = mapBreedToIndex[strBreedThree] ?: 0
        selectedBreed = fkBreedOne
        idDogCreated = dbManager.addDog(
            fkBreedOne, fkBreedTwo, fkBreedThree, percentageBreedOne,
            percentageBreedTwo, percentageBreedThree, selectedBreed, uri
        )
        dbManager.close()
    }

    private fun analyzeImage() {
        try {
            labels.addAll(loadLabels())
        } catch (e: IOException) {
            e.printStackTrace()
        }



        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(0f, 255f))
            .build()

        val bitmap = (dogPhoto.drawable as BitmapDrawable).bitmap
        var tImage = TensorImage(DataType.FLOAT32)
        tImage.load(bitmap)
        tImage = imageProcessor.process(tImage)

        val probabilityBuffer = Array(1) { FloatArray(120) }
        var tflite: Interpreter? = null

        try {
            val tfliteModel = FileUtil.loadMappedFile(this, "tf_model.tflite")
            tflite = Interpreter(tfliteModel)
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading model", e)
        }

        if (tflite != null) {
            tflite.run(tImage.buffer, probabilityBuffer)
        }

        val inferredValues = probabilityBuffer[0]

        for (i in labels.indices) {
            resultsMap[labels[i]] = inferredValues[i]
            mapBreedToIndex[labels[i]] = i + 1
        }

        resultsMap = sortByValue(resultsMap)
        var i = 0
        for (key in resultsMap.keys) {
            if (i == 0) {
                strBreedOne = key
                percentageBreedOne = resultsMap[strBreedOne]!! * 100
                breedOne.text = "$key $percentageBreedOne %"
            }

            if (i == 1) {
                strBreedTwo = key
                percentageBreedTwo = resultsMap[strBreedTwo]!! * 100
                breedTwo.text = "$key $percentageBreedTwo %"
            }

            if (i == 2) {
                strBreedThree = key
                percentageBreedThree = resultsMap[strBreedThree]!! * 100
                breedThree.text = "$key $percentageBreedThree %"
            }

            if (i == 3) break
            i++
        }
        saveResults()
        setDogData()
        progress.dismiss()
    }

    private fun setDogData() {
        val dbManager = DBManager(this, this)
        dbManager.open()
        val dogData = dbManager.getDogData(selectedBreed)
        dbManager.close()

        height.text = "Height: ${dogData?.height}"
        weight.text = "Weight: ${dogData?.weight}"
        origin.text = "Origin: ${dogData?.origin}"
        lifeSpan.text = "Life Span: ${dogData?.lifeSpan}"
        temperament.text = dogData?.temperament
        getHealthIssues(dogData?.health ?: "", dogHealthIssues)
    }


    private fun dogsBreedsListeners() {
        breedOne.setOnClickListener {
            selectedBreed = mapBreedToIndex[strBreedOne] ?: 0
            setDogData()
            breedOne.setTextColor(resources.getColor(R.color.blue_dockdeck))
            breedTwo.setTextColor(resources.getColor(R.color.black))
            breedThree.setTextColor(resources.getColor(R.color.black))
            updateSelectedBreed(idDogCreated, selectedBreed)
        }

        breedTwo.setOnClickListener {
            selectedBreed = mapBreedToIndex[strBreedTwo] ?: 0
            setDogData()
            breedOne.setTextColor(resources.getColor(R.color.black))
            breedTwo.setTextColor(resources.getColor(R.color.blue_dockdeck))
            breedThree.setTextColor(resources.getColor(R.color.black))
            updateSelectedBreed(idDogCreated, selectedBreed)
        }

        breedThree.setOnClickListener {
            selectedBreed = mapBreedToIndex[strBreedThree] ?: 0
            setDogData()
            breedOne.setTextColor(resources.getColor(R.color.black))
            breedTwo.setTextColor(resources.getColor(R.color.black))
            breedThree.setTextColor(resources.getColor(R.color.blue_dockdeck))
            updateSelectedBreed(idDogCreated, selectedBreed)
        }

        share.setOnClickListener {
            try {
                // TODO:  Make a class "ShareDogData"
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateSelectedBreed(dogId: Int, selectedBreedId: Int) {
        val dbManager = DBManager(this, this)
        dbManager.open()
        dbManager.updateDog(dogId, selectedBreedId)
        dbManager.close()
    }

    private fun getHealthIssues(health: String, linearLayout: LinearLayout) {
        linearLayout.removeAllViews()
        val arrOfStr = health.split("\\|").toTypedArray()
        for (i in 1 until arrOfStr.size) {
            val lInflater = LayoutInflater.from(this)
            val view: View = lInflater.inflate(R.layout.dog_health_issue, null)
            val issue: TextView = view.findViewById(R.id.dogIssue)
            issue.text = arrOfStr[i]
            linearLayout.addView(issue)
        }
    }
}