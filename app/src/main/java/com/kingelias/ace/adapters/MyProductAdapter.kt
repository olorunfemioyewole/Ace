package com.kingelias.ace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.kingelias.ace.R
import com.kingelias.ace.data.Product
import com.kingelias.ace.fragments.MyAdsFragment

class MyProductAdapter(private  val context: MyAdsFragment)
    : RecyclerView.Adapter<MyProductAdapter.ViewHolder>() {
    private var productList = mutableListOf<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.title

        if(product.boosted){
            holder.boosted.visibility = View.VISIBLE
        }

        holder.locationLL.visibility = View.GONE
        holder.close.visibility = View.VISIBLE

        val priceText = "GHC "+product.price.toString()
        holder.price.text = priceText

        holder.condition.text = product.condition
        holder.location.text = product.location

        Glide.with(context)
            .load(product.imageUrls?.get(0))
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            context.goToEdit(product)
        }

        holder.close.setOnClickListener {
            AlertDialog.Builder(context.requireContext()).also{
                it.setTitle("Delete this Ad?")
                it.setMessage("Are you sure you want to delete this Ad?")
                it.setNegativeButton("No"){_,_ ->}
                it.setPositiveButton("Yes"){ _, _ ->
                    context.deleteAd(product)
                }
            }.create().show()

        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val image: ImageView = ItemView.findViewById(R.id.product_imageIV)
        val name: TextView = ItemView.findViewById(R.id.titleTV)
        val boosted: TextView = ItemView.findViewById(R.id.boostedTV)
        val price: TextView = ItemView.findViewById(R.id.priceTV)
        val condition: TextView = ItemView.findViewById(R.id.conditionTV)
        val location: TextView = ItemView.findViewById(R.id.locationTV)
        val close: MaterialButton = ItemView.findViewById(R.id.closeBn)
        val locationLL: LinearLayout = ItemView.findViewById(R.id.locLL)
    }

    fun setProducts(products: List<Product>) {
        this.productList = products as MutableList<Product>
        notifyDataSetChanged()
    }
}