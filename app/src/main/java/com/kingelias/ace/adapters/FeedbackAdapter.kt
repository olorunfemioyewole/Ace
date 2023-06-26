package com.kingelias.ace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.kingelias.ace.R
import com.kingelias.ace.data.Feedback
import com.kingelias.ace.fragments.FeedbackFragment

class FeedbackAdapter (private  val context: FeedbackFragment)
:RecyclerView.Adapter<FeedbackAdapter.ViewHolder>(){
    private var  feedbackList= mutableListOf<Feedback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedbackItem = feedbackList[position]

        holder.userName.text = feedbackItem.sender_name
        holder.comment.text = feedbackItem.comment
        val ratingText = "${feedbackItem.rating.toString()}⭐"
        holder.rating.text = ratingText

        Glide.with(context)
            .load(feedbackItem.sender_pfp)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.profilePic)
    }

    override fun getItemCount(): Int {
        return  feedbackList.size
    }
    class ViewHolder(ItemView: View) :RecyclerView.ViewHolder(ItemView)  {
        val profilePic : ShapeableImageView = ItemView.findViewById(R.id.sender_pfpSIV)
        val userName: TextView = ItemView.findViewById(R.id.usernameTV)
        val rating: TextView = ItemView.findViewById(R.id.ratingTV)
        val comment: TextView = ItemView.findViewById(R.id.reviewTV)
    }

    fun setFeedback(feedback:List<Feedback>) {
        this.feedbackList = feedback as MutableList<Feedback>
        notifyDataSetChanged()
    }

}