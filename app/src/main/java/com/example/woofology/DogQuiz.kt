package com.example.woofology

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import java.util.Random



class DogQuiz : AppCompatActivity() {
    var quizImg: ImageView? = null
    var ansIcon: ImageView? = null
    var ansMsg: TextView? = null
    var cancelTxt: TextView? = null
    var continueTxt: TextView? = null
    var btn1: Button? = null
    var btn2: Button? = null
    var btn3: Button? = null
    var btn4: Button? = null
    var client: ApiClient? = null
    var questionContainer: ConstraintLayout? = null
    var answer: String? = null
    var progressDialog: ProgressDialog? = null
    var alertDialog: AlertDialog? = null
    var animationDrawable: Drawable? = null
    var ans = true
    var bottomNavigationView: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_quiz)

        //ActionBar
        val actionBar = supportActionBar
        actionBar?.hide()
        quizImg = findViewById<ImageView>(R.id.iv_quiz)
        questionContainer = findViewById<ConstraintLayout>(R.id.constraint_question_container)
        btn1 = findViewById<Button>(R.id.btn_answer_1)
        btn2 = findViewById<Button>(R.id.btn_answer_2)
        btn3 = findViewById<Button>(R.id.btn_answer_3)
        btn4 = findViewById<Button>(R.id.btn_answer_4)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        progressDialog = ProgressDialog(this@DogQuiz)
        progressDialog!!.setTitle("Loading")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
        client = ApiClient(this)
        client.getRandomBreed(listener)
        bottomNavigationView.setSelectedItemId(R.id.quiz_activity)
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_dog_activity -> {
                    val analysisIntent = Intent(this@DogQuiz, MyDogsActivity::class.java)
                    startActivity(analysisIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.list_activity -> {
                    val allBreedsIntent = Intent(this@DogQuiz, AllBreeds::class.java)
                    startActivity(allBreedsIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.random_activity -> {
                    val randomIntent = Intent(this@DogQuiz, RandomDog::class.java)
                    startActivity(randomIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.quiz_activity -> return@OnNavigationItemSelectedListener true
                R.id.downloaded_images_activity -> {
                    val downloadsIntent = Intent(this@DogQuiz, Downloads::class.java)
                    startActivity(downloadsIntent)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        btn1.setOnClickListener(View.OnClickListener {
            if (!answer.contentEquals(btn1.getText())) {
                btn1.setBackgroundColor(resources.getColor(R.color.wrongAnswer))
                ans = false
            }
            if (btn1.getText() == answer) {
                btn1.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn2.getText() == answer) {
                btn2.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn3.getText() == answer) {
                btn3.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn4.getText() == answer) {
                btn4.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            quizDialog()
        })
        btn2.setOnClickListener(View.OnClickListener {
            if (!answer.contentEquals(btn2.getText())) {
                btn2.setBackgroundColor(resources.getColor(R.color.wrongAnswer))
                ans = false
            }
            if (btn1.getText() == answer) {
                btn1.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn2.getText() == answer) {
                btn2.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn3.getText() == answer) {
                btn3.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn4.getText() == answer) {
                btn4.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            quizDialog()
        })
        btn3.setOnClickListener(View.OnClickListener {
            if (!answer.contentEquals(btn3.getText())) {
                btn3.setBackgroundColor(resources.getColor(R.color.wrongAnswer))
                ans = false
            }
            if (btn1.getText() == answer) {
                btn1.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn2.getText() == answer) {
                btn2.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn3.getText() == answer) {
                btn3.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn4.getText() == answer) {
                btn4.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            quizDialog()
        })
        btn4.setOnClickListener(View.OnClickListener {
            if (!answer.contentEquals(btn4.getText())) {
                btn4.setBackgroundColor(resources.getColor(R.color.wrongAnswer))
                ans = false
            }
            if (btn1.getText() == answer) {
                btn1.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn2.getText() == answer) {
                btn2.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn3.getText() == answer) {
                btn3.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            if (btn4.getText() == answer) {
                btn4.setBackgroundColor(resources.getColor(R.color.rightAnswer))
            }
            quizDialog()
        })
    }

    private fun showRandomBreeds(response: RandomBreedResponse) {
        val r = Random()
        val i1 = r.nextInt(5 - 1) + 1
        Picasso.get().load(response.message).into(quizImg)
        answer = Common.getBreedFromLink(response.message.toString())
        if (i1 == 1) {
            btn1!!.text = answer
            btn2!!.text = randomBreed
            btn3!!.text = randomBreed
            btn4!!.text = randomBreed
        } else if (i1 == 2) {
            btn2!!.text = answer
            btn1!!.text = randomBreed
            btn3!!.text = randomBreed
            btn4!!.text = randomBreed
        } else if (i1 == 3) {
            btn3!!.text = answer
            btn1!!.text = randomBreed
            btn2!!.text = randomBreed
            btn4!!.text = randomBreed
        } else {
            btn4!!.text = answer
            btn1!!.text = randomBreed
            btn2!!.text = randomBreed
            btn3!!.text = randomBreed
        }
        questionContainer!!.visibility = View.VISIBLE
        progressDialog!!.dismiss()
    }

    val randomBreed: String
        get() {
            val r = Random()
            val randIndex = r.nextInt(Common.breeds.size)
            return Common.breeds.get(randIndex)
        }
    private val listener: RandomBreedListener = object : RandomBreedListener() {
        fun onFetch(response: RandomBreedResponse, message: String?) {
            if (response.message.isEmpty()) {
                Toast.makeText(this@DogQuiz, "No Dog Breeds Available", Toast.LENGTH_SHORT).show()
                return
            }
            showRandomBreeds(response)
        }

        fun onError(message: String?) {
            Toast.makeText(this@DogQuiz, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun quizDialog() {
        val alert = AlertDialog.Builder(this@DogQuiz)
        val view: View = layoutInflater.inflate(R.layout.dialog_quiz, null)
        ansIcon = view.findViewById<View>(R.id.iv_animation) as ImageView
        ansMsg = view.findViewById<View>(R.id.tv_message) as TextView
        cancelTxt = view.findViewById<View>(R.id.cancel_txt) as TextView
        continueTxt = view.findViewById<View>(R.id.continue_txt) as TextView
        alert.setView(view)
        alertDialog = alert.create()
        animationDrawable = ContextCompat.getDrawable(view.context, R.drawable.animated_false_48)
        if (!ans) {
            animationDrawable =
                ContextCompat.getDrawable(view.context, R.drawable.animated_false_48_outline)
            ansIcon!!.setImageDrawable(animationDrawable)
            ansMsg!!.text = "Oops! Wrong answer"
        } else {
            animationDrawable =
                ContextCompat.getDrawable(view.context, R.drawable.animated_true_48_outline)
            ansIcon!!.setImageDrawable(animationDrawable)
            ansMsg!!.text = "Yayy!! You got it right!"
        }
        continueTxt!!.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        cancelTxt!!.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }
}