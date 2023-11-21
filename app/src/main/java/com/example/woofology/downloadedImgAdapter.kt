package com.example.woofology

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class downloadedImgAdapter(
    var context: Context,
    var list: ArrayList<DownloadedPictures>,
    var onClick: onItemClick
) : RecyclerView.Adapter<downloadedImgAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_breed_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bitmap = BitmapFactory.decodeFile(list[position].path)
        holder.img.setImageBitmap(bitmap)
        val link = list[position].path
        holder.img.setOnClickListener { onClick.onImgItemClick(link, position) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView

        init {
            img = itemView.findViewById(R.id.image_breed)
        }
    }
}