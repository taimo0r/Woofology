package com.example.woofology

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woofology.Database.DBManager
import com.example.woofology.ModelClasses.Dog
import java.util.LinkedList

class DogRVListAdapter(private val context: Context, private val activity: Activity, private val dogList: LinkedList<Dog>) :
    RecyclerView.Adapter<DogRVListAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var origin: TextView = view.findViewById(R.id.origin)
        var percentages: TextView = view.findViewById(R.id.percentages)
        var thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        var viewBackground: RelativeLayout = view.findViewById(R.id.view_background)
        var viewForeground: LinearLayout = view.findViewById(R.id.view_foreground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dog_rv_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dog = dogList[position]
        val imageCameraBitmap = BitmapFactory.decodeFile(dog.uriImage)
        holder.thumbnail.setImageBitmap(imageCameraBitmap)
        holder.name.text = dog.selectedBreedStr
        val dbManager = DBManager(context, context as Activity)
        dbManager.open()
        val data = dbManager.getDogData(dog.selectedBreed)
        dbManager.close()
        holder.origin.text = data?.origin
        holder.percentages.text = "${dog.percentageBreedOne}/${dog.percentageBreedTwo}/${dog.percentageBreedThree}"
        createListeners(holder, dog)
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    fun removeItem(position: Int) {
        dogList.removeAt(position)
        (activity as MyDogsActivity).removeAddDogsMessage()
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Dog, position: Int) {
        dogList.add(position, item)
        notifyItemInserted(position)
    }

    private fun createListeners(holder: MyViewHolder, dog: Dog) {
        holder.viewForeground.setOnClickListener {
            val intent = Intent(context, UpdateDogActivity::class.java)
            val b = Bundle()
            b.putInt("idDog", dog.id) // Your id
            intent.putExtras(b) // Put your id to your next Intent
            context.startActivity(intent)
        }
    }
}