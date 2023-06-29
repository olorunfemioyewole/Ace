package com.kingelias.ace.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.fragments.NewAdFragment

class SelectedImageAdapter(
    private val imageSlide: List<Uri>, private val context: NewAdFragment
) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.selected_product_image_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(imageSlide[position])
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.slideImage)

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return imageSlide.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slideImage: ImageView = itemView.findViewById(R.id.selectionIV)

    }

}