package com.example.woofology

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class AllBreedsAdapter(var context: Context, breedList: MutableList<String>) :

    RecyclerView.Adapter<AllBreedsAdapter.ViewHolder>(), Filterable {
    var breedList: List<String>
    lateinit var breedListAll: List<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_dog_breed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = breedList[position]
        holder.textView.text = name.uppercase(Locale.getDefault())
        holder.textView.setOnClickListener {
            val intent = Intent(context, BreedImages::class.java)
            intent.putExtra("name", name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return breedList.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter: Filter = object : Filter() {
        //run on background
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: MutableList<String> = ArrayList()
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(breedList)
            } else {
                for (breed in breedListAll) {
                    if (breed.lowercase(Locale.getDefault()).contains(
                            charSequence.toString().lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) {
                        filteredList.add(breed)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        //run on UI thread
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            breedList.clear()
            breedList.addAll((filterResults.values as Collection<String>))
            notifyDataSetChanged()
        }
    }

    init {
        this.breedList = breedList
        breedListAll = ArrayList(breedList)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView

        init {
            textView = itemView.findViewById<TextView>(R.id.textview_breed)
        }
    }
}