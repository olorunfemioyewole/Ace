package com.kingelias.ace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kingelias.ace.R
import com.kingelias.ace.data.Product
import com.kingelias.ace.data.Specification

class SpecificationAdapter()
    : RecyclerView.Adapter<SpecificationAdapter.ViewHolder>() {
    private var specsList = mutableListOf<Specification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.specs_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spec = specsList[position]

        holder.label.text = spec.label
        holder.value.text = spec.value
    }

    override fun getItemCount(): Int {
        return specsList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val label: TextView = ItemView.findViewById(R.id.spec_labelTV)
        val value: TextView = ItemView.findViewById(R.id.spec_valueTV)
    }

    fun setSpecs(products: Product) {
        val specs = mutableListOf<Specification>()

        if (products.brand?.isNotEmpty() == true){
            specs.add(Specification("Brand", products.brand!!))
        }
        if(products.condition?.isNotEmpty() == true){
            specs.add(Specification("Condition", products.condition!!))
        }
        if(products.product_type?.isNotEmpty() == true){
            specs.add(Specification("Type", products.product_type!!))
        }
        if(products.subtype?.isNotEmpty() == true){
            specs.add(Specification("Subtype", products.subtype!!))
        }
        if(products.system_configuration?.isNotEmpty() == true){
            specs.add(Specification("System Configuration", products.system_configuration!!))
        }
        if(products.color?.isNotEmpty() == true){
            specs.add(Specification("Color", products.color!!))
        }
        if(products.output_power != null){
            specs.add(Specification("Output Power", products.output_power.toString()))
        }
        if(products.connectivity?.isNotEmpty() == true){
            specs.add(Specification("Connectivity", products.connectivity!!))
        }
        if(products.form_factor?.isNotEmpty() == true){
            specs.add(Specification("Form Factor", products.form_factor!!))
        }
        if(products.polar_pattern?.isNotEmpty() == true){
            specs.add(Specification("Polar Pattern", products.polar_pattern!!))
        }
        if(products.number_of_channels?.isNotEmpty() == true){
            specs.add(Specification("Number of Channels", products.number_of_channels!!))
        }
        if(products.amplification_type?.isNotEmpty() == true){
            specs.add(Specification("Amplification Type", products.amplification_type!!))
        }
        if(products.number_of_pads != null){
            specs.add(Specification("Number of Pads", products.number_of_pads.toString()))
        }
        if(products.controller_pads?.isNotEmpty() == true){
            specs.add(Specification("Controller Pads", products.controller_pads!!))
        }
        if(products.range != null){
            specs.add(Specification("Range", products.range.toString()))
        }
        if(products.drive_system?.isNotEmpty() == true){
            specs.add(Specification("Drive System", products.drive_system!!))
        }
        if(products.type_of_cartridge?.isNotEmpty() == true){
            specs.add(Specification("Type of Cartridge", products.type_of_cartridge!!))
        }
        if(products.connector_1_type?.isNotEmpty() == true){
            specs.add(Specification("Connector 1 Type", products.connector_1_type!!))
        }
        if(products.connector_2_type?.isNotEmpty() == true){
            specs.add(Specification("Connector 2 Type", products.connector_2_type!!))
        }
        if(products.keyboard_switches?.isNotEmpty() == true){
            specs.add(Specification("Keyboard Switches", products.keyboard_switches!!))
        }
        if(products.cable_length != null){
            specs.add(Specification("Cable Length (m)", products.cable_length.toString()))
        }
        if(products.device_interface?.isNotEmpty() == true){
            specs.add(Specification("Device Interface", products.device_interface!!))
        }
        if(products.number_of_buttons != null){
            specs.add(Specification("Number of Buttons", products.number_of_buttons.toString()))
        }
        if(products.backlighting?.isNotEmpty() == true){
            specs.add(Specification("Backlighting", products.backlighting!!))
        }
        if(products.cable_type?.isNotEmpty() == true){
            specs.add(Specification("Cable Type", products.cable_type!!))
        }

        this.specsList = specs
        notifyDataSetChanged()
    }
}