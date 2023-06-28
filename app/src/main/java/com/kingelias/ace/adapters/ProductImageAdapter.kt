package com.kingelias.ace.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kingelias.ace.R

class ProductImageAdapter(
    private val imageSlide: List<String>, private val context: Context
) : RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.product_image_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageSlide[position])
    }

    override fun getItemCount(): Int {
        return imageSlide.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slideImage: ImageView = itemView.findViewById(R.id.imageIV)

        fun bind(image: String) {
            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(slideImage)

        }
    }

}