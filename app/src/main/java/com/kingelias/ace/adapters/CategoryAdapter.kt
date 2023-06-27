package com.kingelias.ace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.data.Category
import com.kingelias.ace.fragments.HomeFragment

class CategoryAdapter(private  val context: HomeFragment)
    : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    private var  categoryList= mutableListOf<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryItem = categoryList[position]

        holder.name.text = categoryItem.name

        Glide.with(context)
            .load(categoryItem.image)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            context.goToSubcategories(categoryItem)
        }
    }

    override fun getItemCount(): Int {
        return  categoryList.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)  {
        val image : ImageView = ItemView.findViewById(R.id.category_imageIV)
        val name: TextView = ItemView.findViewById(R.id.category_nameTV)
    }

    fun setCategories(categories:List<Category>) {
        this.categoryList = categories as MutableList<Category>
        notifyDataSetChanged()
    }

}