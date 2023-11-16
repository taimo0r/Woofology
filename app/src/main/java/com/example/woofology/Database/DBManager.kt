package com.example.woofology.Database

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.example.woofology.ModelClasses.Dog
import com.example.woofology.ModelClasses.DogData
import java.util.LinkedList

class DBManager(context: Context, private val activity: Activity) {
    private var dbHelper: DatabaseHelper? = null
    private val context: Context
    private var database: SQLiteDatabase? = null

    init {
        this.context = context
    }

    @Throws(SQLException::class)
    fun open(): DBManager {
        dbHelper = DatabaseHelper(context, activity)
        database = dbHelper!!.writableDatabase
        return this
    }

    fun close() {
        dbHelper!!.close()
    }

    fun addDog(
        breedOne: Int, breedTwo: Int, breedThree: Int,
        percentageBreedOne: Float, percentageBreedTwo: Float,
        percentageBreedThree: Float, selectedBreed: Int, uriImage: String
    ): Int {
        val contentValue = ContentValues()
        contentValue.put(DatabaseHelper.BREED_ONE, breedOne)
        contentValue.put(DatabaseHelper.BREED_TWO, breedTwo)
        contentValue.put(DatabaseHelper.BREED_THREE, breedThree)
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_ONE, percentageBreedOne)
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_TWO, percentageBreedTwo)
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_THREE, percentageBreedThree)
        contentValue.put(DatabaseHelper.SELECTED_BREED, selectedBreed)
        contentValue.put(DatabaseHelper.URI_IMAGE, uriImage)

        val id = database!!.insert(DatabaseHelper.DOGS, null, contentValue).toInt()

        return id
    }

    fun getDog(id: Int): Dog {
        val columns = arrayOf(
            DatabaseHelper.ID_DOGS, DatabaseHelper.BREED_ONE,
            DatabaseHelper.BREED_TWO, DatabaseHelper.BREED_THREE,
            DatabaseHelper.PERCENTAGE_BREED_ONE, DatabaseHelper.PERCENTAGE_BREED_TWO,
            DatabaseHelper.PERCENTAGE_BREED_THREE, DatabaseHelper.SELECTED_BREED,
            DatabaseHelper.URI_IMAGE
        )

        val cursor = database!!.query(
            DatabaseHelper.DOGS, columns,
            DatabaseHelper.ID_DOGS + "=" + id,
            null, null, null,
            null, null
        )

        cursor.moveToFirst()
        return createDog(cursor)
    }

    fun getDogData(id: Int): DogData? {
        val columns = arrayOf(
            DatabaseHelper.NAME, DatabaseHelper.ORIGIN,
            DatabaseHelper.HEIGHT, DatabaseHelper.WEIGHT,
            DatabaseHelper.LIFE_SPAN, DatabaseHelper.TEMPERAMENT,
            DatabaseHelper.HEALTH
        )

        val cursor = database!!.query(
            DatabaseHelper.DOG_BREEDS, columns,
            DatabaseHelper.ID_DOGS_BREEDS + "=" + id,
            null, null, null,
            null, null
        )

        return cursor?.use {
            it.moveToFirst()
            DogData(
                it.getString(0),
                it.getString(1),
                it.getString(2),
                it.getString(3),
                it.getString(4),
                it.getString(5),
                it.getString(6)
            )
        }
    }

    fun updateDog(id: Int, selectedBreed: Int): Int {
        val contentValues = ContentValues()
        contentValues.put(DatabaseHelper.SELECTED_BREED, selectedBreed)
        return database!!.update(DatabaseHelper.DOGS, contentValues, DatabaseHelper.ID_DOGS + " = $id", null)
    }

    fun getDogs(): LinkedList<Dog> {
        val dogs: LinkedList<Dog> = LinkedList()

        val columns = arrayOf(
            DatabaseHelper.ID_DOGS, DatabaseHelper.BREED_ONE,
            DatabaseHelper.BREED_TWO, DatabaseHelper.BREED_THREE,
            DatabaseHelper.PERCENTAGE_BREED_ONE, DatabaseHelper.PERCENTAGE_BREED_TWO,
            DatabaseHelper.PERCENTAGE_BREED_THREE, DatabaseHelper.SELECTED_BREED,
            DatabaseHelper.URI_IMAGE
        )

        val cursor = database!!.query(
            DatabaseHelper.DOGS, columns,
            null, null, null, null,
            "${DatabaseHelper.ID_DOGS} DESC"
        )

        while (cursor.moveToNext()) {
            val dogItem = createDog(cursor)
            dogs.add(dogItem)
        }
        cursor.close()
        return dogs
    }

    fun deleteDog(id: Int) {
        database!!.delete(DatabaseHelper.DOGS, "${DatabaseHelper.ID_DOGS}=$id", null)
    }

    private fun createDog(cursor: Cursor): Dog {
        val idDog = cursor.getInt(0)
        val breedOneId = cursor.getInt(1)
        val breedTwoId = cursor.getInt(2)
        val breedThreeId = cursor.getInt(3)
        val percentageBreedOne = cursor.getFloat(4)
        val percentageBreedTwo = cursor.getFloat(5)
        val percentageBreedThree = cursor.getFloat(6)
        val selectedBreed = cursor.getInt(7)
        val uriImage = cursor.getString(8)

        val breedDogOne = getDogData(breedOneId)
        val breedDogTwo = getDogData(breedTwoId)
        val breedDogThree = getDogData(breedThreeId)
        var selected: DogData? = null

        if (breedOneId == selectedBreed)
            selected = breedDogOne

        if (breedTwoId == selectedBreed)
            selected = breedDogTwo

        if (breedThreeId == selectedBreed)
            selected = breedDogThree

        val breedOneStr = breedDogOne?.name ?: ""
        val breedTwoStr = breedDogTwo?.name ?: ""
        val breedThreeStr = breedDogThree?.name ?: ""
        val percentageBreedOneStr = String.format("%.5f", percentageBreedOne)
        val percentageBreedTwoStr = String.format("%.5f", percentageBreedTwo)
        val percentageThreeStr = String.format("%.5f", percentageBreedThree)
        val selectedBreedStr = selected?.name ?: ""

        return Dog(
            idDog, breedOneStr, breedTwoStr, breedThreeStr, percentageBreedOneStr,
            percentageBreedTwoStr, percentageThreeStr, selectedBreedStr, selectedBreed, uriImage,
            breedOneId, breedTwoId, breedThreeId
        )
    }
}