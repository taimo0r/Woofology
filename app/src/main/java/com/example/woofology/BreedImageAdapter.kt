package com.example.woofology

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BreedImageAdapter(
    var context: Context,
    var breedImagesList: List<String>,
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
        Picasso.get().load(breedImagesList[position]).placeholder(R.drawable.dog_icon_svg)
            .into(holder.breedImage)
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
    }
}