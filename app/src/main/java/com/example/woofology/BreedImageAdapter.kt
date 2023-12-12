package com.example.woofology

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BreedImageAdapter(
    var context: Context,
    var breedImagesList: MutableList<String>,
    onClick: onItemClick
) : RecyclerView.Adapter<BreedImageAdapter.ViewHolder?>() {
    var onClick: onItemClick

    init {
        this.onClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_breed_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val link = breedImagesList[position]

        Picasso.get()
            .load(link)
            .placeholder(R.drawable.dog_icon_svg)
            .into(holder.breedImage, object : Callback {
                override fun onSuccess() {
                    // Image loaded, show the item
                    holder.rootView.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    // Image failed to load, hide the item
                    val currentPosition = breedImagesList.indexOf(link)
                    if (currentPosition != -1) {
                        breedImagesList.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                        notifyItemRangeChanged(currentPosition, breedImagesList.size)
                    }
                }
            })


//        Picasso.get().load(breedImagesList[position]).placeholder(R.drawable.dog_icon_svg)
//            .into(holder.breedImage)
        Log.d("URL", breedImagesList[position])
        holder.breedImage.setOnClickListener { onClick.onImgItemClick(link, position) }
    }

    override fun getItemCount(): Int {
        return breedImagesList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var breedImage: ImageView

        init {
            breedImage = itemView.findViewById(R.id.image_breed)
        }
        val rootView: View = itemView
    }
}