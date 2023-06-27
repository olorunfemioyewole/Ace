package com.kingelias.ace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDivider
import com.kingelias.ace.R
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.fragments.SubcategoryFragment

class SubcategoryAdapter(private  val context: SubcategoryFragment)
    : RecyclerView.Adapter<SubcategoryAdapter.ViewHolder>(){
    private var  subcategoryList= mutableListOf<Subcategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subcategory_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subcategoryItem = subcategoryList[position]

        holder.name.text = subcategoryItem.name

        holder.itemView.setOnClickListener {
            context.goToSearchResults(subcategoryItem)
        }

        if (position == subcategoryList.lastIndex){
            holder.div.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return  subcategoryList.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)  {
        val name: TextView = ItemView.findViewById(R.id.subcatTV)
        val div: MaterialDivider = ItemView.findViewById(R.id.subcatDIV)
    }

    fun setSubcategories(subcategories:List<Subcategory>) {
        this.subcategoryList = subcategories as MutableList<Subcategory>
        notifyDataSetChanged()
    }

}