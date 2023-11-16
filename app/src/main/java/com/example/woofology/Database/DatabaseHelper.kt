package com.example.woofology.Database

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.IOException
import java.io.InputStreamReader
import com.opencsv.CSVReader


class DatabaseHelper(context: Context, private val activity: Activity) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // Tables Names
        const val DOG_BREEDS = "DOG_BREEDS"
        const val DOGS = "DOGS"

        // Table DOG_BREEDS columns
        const val ID_DOGS_BREEDS = "id"
        const val NAME = "name"
        const val ORIGIN = "origin"
        const val HEIGHT = "height"
        const val WEIGHT = "weight"
        const val LIFE_SPAN = "life_span"
        const val TEMPERAMENT = "temperament"
        const val HEALTH = "health"

        // Table DOGS columns
        const val ID_DOGS = "id"
        const val BREED_ONE = "breedOne"
        const val BREED_TWO = "breedTwo"
        const val BREED_THREE = "breedThree"
        const val PERCENTAGE_BREED_ONE = "percentageBreedOne"
        const val PERCENTAGE_BREED_TWO = "percentageBreedTwo"
        const val PERCENTAGE_BREED_THREE = "percentageBreedThree"
        const val SELECTED_BREED = "selectedBreed"
        const val URI_IMAGE = "uriImage"

        // Database Information
        const val DB_NAME = "DOGS.DB"

        // database version
        const val DB_VERSION = 1

        // Creating table DOG_BREEDS query
        const val CREATE_TABLE_DOGS_BREED =
            "CREATE TABLE $DOG_BREEDS(" +
                    "$ID_DOGS INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                    "$NAME TEXT NOT NULL," +
                    "$ORIGIN TEXT NOT NULL," +
                    "$HEIGHT TEXT NOT NULL," +
                    "$WEIGHT TEXT NOT NULL," +
                    "$LIFE_SPAN TEXT NOT NULL," +
                    "$TEMPERAMENT TEXT NOT NULL," +
                    "$HEALTH TEXT NOT NULL);"

        // Creating table DOGS query
        const val CREATE_TABLE_DOGS =
            "CREATE TABLE $DOGS(" +
                    "$ID_DOGS_BREEDS INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                    "$BREED_ONE INTEGER NOT NULL," +
                    "$BREED_TWO INTEGER NOT NULL," +
                    "$BREED_THREE INTEGER NOT NULL," +
                    "$PERCENTAGE_BREED_ONE FLOAT NOT NULL," +
                    "$PERCENTAGE_BREED_TWO FLOAT NOT NULL," +
                    "$PERCENTAGE_BREED_THREE FLOAT NOT NULL," +
                    "$SELECTED_BREED INTEGER NOT NULL," +
                    "$URI_IMAGE TEXT NOT NULL," +
                    "FOREIGN KEY($BREED_ONE) REFERENCES $DOG_BREEDS(\"id\")," +
                    "FOREIGN KEY($BREED_TWO) REFERENCES $DOG_BREEDS(\"id\")," +
                    "FOREIGN KEY($BREED_THREE) REFERENCES $DOG_BREEDS(\"id\"));"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_DOGS_BREED)
        db.execSQL(CREATE_TABLE_DOGS)
        fillDogsBreedsDatabase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DOG_BREEDS")
        db.execSQL("DROP TABLE IF EXISTS $DOGS")
        onCreate(db)
    }

    private fun fillDogsBreedsDatabase(db: SQLiteDatabase) {
        try {
            val inputStreamReader = InputStreamReader(activity.assets.open("dogs_clean_data.csv"))
            val reader = CSVReader(inputStreamReader)
            var nextLine: Array<String>?

            nextLine = reader.readNext() // Read headers

            while (reader.readNext().also { nextLine = it } != null) {
                val id = nextLine!![0].toInt()
                val name = nextLine!![1]
                val origin = nextLine!![2]
                val height = "${nextLine!![3].replace("\"", "")} Inches"
                val weight = nextLine!![4]
                val lifeSpan = nextLine!![5]
                val temperament = nextLine!![6]
                val health = nextLine!![7]

                val contentValue = ContentValues()
                contentValue.put(ID_DOGS_BREEDS, id)
                contentValue.put(NAME, name)
                contentValue.put(ORIGIN, origin)
                contentValue.put(HEIGHT, height)
                contentValue.put(WEIGHT, weight)
                contentValue.put(LIFE_SPAN, lifeSpan)
                contentValue.put(TEMPERAMENT, temperament)
                contentValue.put(HEALTH, health)
                db.insert(DOG_BREEDS, null, contentValue)
            }
            inputStreamReader.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("Fill Database Error", "Failed reading the CSV")
        }
    }
}