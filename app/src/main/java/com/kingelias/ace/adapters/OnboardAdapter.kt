package com.kingelias.ace.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.data.OnboardItem

class OnboardAdapter(
    private val onboardSlide: List<OnboardItem>, private val onboardContext: Context
) : RecyclerView.Adapter<OnboardAdapter.OnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboard_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(onboardSlide[position])
    }

    override fun getItemCount(): Int {
        return onboardSlide.size
    }

    inner class OnBoardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val onboardHeading: TextView = itemView.findViewById(R.id.headingTV)
        private val onboardDescription: TextView = itemView.findViewById(R.id.descriptionTV)
        private val slideImage: ImageView = itemView.findViewById(R.id.slideIV)

        fun bind(onboardSlide: OnboardItem) {
            onboardHeading.text = onboardSlide.heading
            onboardDescription.text = onboardSlide.description

            Glide.with(onboardContext)
                .load(onboardSlide.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(slideImage)

        }
    }

}