package com.kingelias.ace.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @get: Exclude
    var id: String? = null,
    var title: String? = "",
    var description: String? = "",
    var condition: String? = "",
    var price: Float? = 1.0F,
    var negotiable: Boolean = false,
    var delivery: Boolean = false,
    var exchange_possible: Boolean = false,
    var boosted: Boolean = false,
    var category: String? = "",
    var subcategory: String? = "",
    var product_type: String? = "",
    var seller_id: String? = "",
    var seller_phone: String? = "0545565901",
    var imageUrls: List<String>? = null,
    var date_posted: String? = "",
    var location: String? = "Greater Accra",
    var wishlisted_by: List<String>? = null,
    var active: Boolean = true,

    //specifications\\
    var brand: String? = null,
    var model: String? = null,

    //sound systems
    var system_configuration: String? = null, //5.0, 7.1 etc | other types: speakers, amplifiers, HiFi Systems, studio monitors,
    var output_power: Int? = null, //other types: speakers, amplifiers, HiFi Systems, studio monitors,
    var connectivity: String? = null, //other types: speakers, microphones, amplifiers, audio interfaces, CD players, Dj Controllers, Equalizers, HiFi Systems, receivers, midi controllers, headphones

    //microphones
    var form_factor: String? = null,
    var polar_pattern: String? = null,

    //music mixers
    var subtype: String? = null, //any, analog or digital other type: walkie talkies,
    var number_of_channels: String? = null,//other types: Dj Controllers, walkie talkies,
    var amplification_type: String? = null,//other types: studio monitors,

    //Dj Controllers
    var number_of_pads: Int? = null,

    //midi controllers
    var controller_pads: String? = null,

    //walkie talkie
    var range: Int? = null,//in km

    //turntables
    var drive_system: String? = null,
    var type_of_cartridge: String? = null,

    //Adapters
    var connector_1_type: String? = null,
    var connector_2_type: String? = null,

    //keyboards
    var keyboard_switches: String? = null,
    var cable_length: Int? = null, //other types: cables,
    var device_interface: String? = null, //other types: mice, capture card, card readers, flash drives, portable SSDs, hard drives, SSDs, NAS, headphones
    var number_of_buttons: Int? = null, //other types: mice,
    var backlighting: String? = null,
    var color: String? = null, // other types: mice, mouse pads, stylus pens, headphones

    //cables
    var cable_type: String? = null, // other types: mice

    //batteries
    var battery_capacity: Int? = null, // other types: UPSs
    var voltage: Float? = null, // other types: UPSs
    var output_energy: Int? = null,

    //mice
    var max_dpi: Int? = null,
    var tracking_method: String? = null,

    //blank cds
    var storage_capacity: String? = null, //other types: flash drives, memory cards, portable SSDs, hard drives, SSDs
    var disc_type: String? = null,
    var max_write_speed: String? = null,

    //capture card
    var capture_card_type: String? = null,
    var max_video_resolution: String? = null,//other types: splitters
    var max_frame_rate: String? = null,
    var cc_number_of_channels: String? = null,
    var hdr_support: String? = null,

    //card readers
    var supported_cards: String? = null,
    var number_of_slots: Int? = null, //other types: docking stations, usb hubs

    //docking stations
    var power_demand: Int? = null, //watts
    var compatibility: String? = null, //other types: stylus pens
    var wireless_charging: String? = null,

    //dongles
    var wifi_speed: Int? = null, //MBps other types: usb wifi adapters,
    var lan_port_speed: Int? = null, // other types: usb wifi adapters,
    var net_technology: String? = null, // other types: usb wifi adapters,
    var bluetooth_version: String? = null, // other types: usb wifi adapters,

    //flash drives
    var read_speed: String? = null, //other types: memory cards, portable SSDs, hard drives, SSDs
    var write_speed: String? = null, //other types: memory cards, portable SSDs, hard drives, SSDs

    //hard drive enclosure
    var hard_drive_size: String? = null, //other types: hard drives

    //splitters
    var input_type: String? = null,
    var output_type: String? = null,

    //laptop cases and bags
    var laptop_screen_size: String? = null, //inches
    var material: String? = null, //other types: mouse pads
    var closure_type: String? = null,

    //memory cards
    var memory_card_type: String? = null,

    //mouse pads
    var length: Float? = null, // in cm | other types: computer cases, Server Racks
    var width: Float? = null, // in cm | other types: computer cases, Server Racks
    var thickness: Int? = null, // in mm

    //stylus pens
    var tip_size: Int? = null, // in mm
    var battery_life: Int? = null, // in hours

    //usb wifi adapters
    var wifi_standard: String? = null,

    //hard drives
    var drive_subtype: String? = null,
    var rotation_speed: Int? = null, // in rpm

    //UPSs
    var wattage: Int? = null, // in watts
    var number_of_outlets: Int? = null,
    var ups_power: Int? = null, // in VA

    //SSDs
    var usage: String? = null, //other types: memory, CPU cooler

    //graphic cards
    var memory_size: String? = null, //other types: memory
    var memory_type: String? = null, //other types: memory
    var graphic_card_interface: String? = null,

    //memory
    var max_memory_frequency: Int? = null,
    var number_of_modules: Int? = null,
    var latency_timing: String? = null,

    //computer cases
    var motherboard_form_factor: String? = null,
    var case_type: String? = null,
    var number_of_fans: Int? = null,

    //CPU Processors
    var number_of_cores: Int? = null,
    var number_of_thread: Int? = null,
    var has_igpu: String? = null,
    var socket_type: String? = null,// CPU cooler, motherboards
    var max_tdp: Int? = null,

    //motherboards
    var number_of_memory_slots: Int? = null,
    var number_of_m2_slots: Int? = null,
    var brand_compatibility: Int? = null,

    //network cards
    var number_of_ports: Int? = null,

    //NAS
    var number_of_bays: Int? = null,

    //power supplies
    var power: Int? = null,
    var efficiency_rating: String? = null,

    //Server Racks
    var height: Float? = null, // in cm
    var weight: Int? = null, // in kg
    var mount_type: String? = null,
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (condition != other.condition) return false
        if (price != other.price) return false
        if (negotiable != other.negotiable) return false
        if (exchange_possible != other.exchange_possible) return false
        if (boosted != other.boosted) return false
        if (category != other.category) return false
        if (subcategory != other.subcategory) return false
        if (product_type != other.product_type) return false
        if (seller_id != other.seller_id) return false
        if (seller_phone != other.seller_phone) return false
        if (imageUrls != other.imageUrls) return false
        if (date_posted != other.date_posted) return false
        if (location != other.location) return false
        if (wishlisted_by != other.wishlisted_by) return false
        if (brand != other.brand) return false
        if (model != other.model) return false
        if (system_configuration != other.system_configuration) return false
        if (output_power != other.output_power) return false
        if (connectivity != other.connectivity) return false
        if (form_factor != other.form_factor) return false
        if (polar_pattern != other.polar_pattern) return false
        if (subtype != other.subtype) return false
        if (number_of_channels != other.number_of_channels) return false
        if (amplification_type != other.amplification_type) return false
        if (number_of_pads != other.number_of_pads) return false
        if (controller_pads != other.controller_pads) return false
        if (range != other.range) return false
        if (drive_system != other.drive_system) return false
        if (type_of_cartridge != other.type_of_cartridge) return false
        if (connector_1_type != other.connector_1_type) return false
        if (connector_2_type != other.connector_2_type) return false
        if (keyboard_switches != other.keyboard_switches) return false
        if (cable_length != other.cable_length) return false
        if (device_interface != other.device_interface) return false
        if (number_of_buttons != other.number_of_buttons) return false
        if (backlighting != other.backlighting) return false
        if (color != other.color) return false
        if (cable_type != other.cable_type) return false
        if (battery_capacity != other.battery_capacity) return false
        if (voltage != other.voltage) return false
        if (output_energy != other.output_energy) return false
        if (max_dpi != other.max_dpi) return false
        if (tracking_method != other.tracking_method) return false
        if (storage_capacity != other.storage_capacity) return false
        if (disc_type != other.disc_type) return false
        if (max_write_speed != other.max_write_speed) return false
        if (capture_card_type != other.capture_card_type) return false
        if (max_video_resolution != other.max_video_resolution) return false
        if (max_frame_rate != other.max_frame_rate) return false
        if (cc_number_of_channels != other.cc_number_of_channels) return false
        if (hdr_support != other.hdr_support) return false
        if (supported_cards != other.supported_cards) return false
        if (number_of_slots != other.number_of_slots) return false
        if (power_demand != other.power_demand) return false
        if (compatibility != other.compatibility) return false
        if (wireless_charging != other.wireless_charging) return false
        if (wifi_speed != other.wifi_speed) return false
        if (lan_port_speed != other.lan_port_speed) return false
        if (net_technology != other.net_technology) return false
        if (bluetooth_version != other.bluetooth_version) return false
        if (read_speed != other.read_speed) return false
        if (write_speed != other.write_speed) return false
        if (hard_drive_size != other.hard_drive_size) return false
        if (input_type != other.input_type) return false
        if (output_type != other.output_type) return false
        if (laptop_screen_size != other.laptop_screen_size) return false
        if (material != other.material) return false
        if (closure_type != other.closure_type) return false
        if (memory_card_type != other.memory_card_type) return false
        if (length != other.length) return false
        if (width != other.width) return false
        if (thickness != other.thickness) return false
        if (tip_size != other.tip_size) return false
        if (battery_life != other.battery_life) return false
        if (wifi_standard != other.wifi_standard) return false
        if (drive_subtype != other.drive_subtype) return false
        if (rotation_speed != other.rotation_speed) return false
        if (wattage != other.wattage) return false
        if (number_of_outlets != other.number_of_outlets) return false
        if (ups_power != other.ups_power) return false
        if (usage != other.usage) return false
        if (memory_size != other.memory_size) return false
        if (memory_type != other.memory_type) return false
        if (graphic_card_interface != other.graphic_card_interface) return false
        if (max_memory_frequency != other.max_memory_frequency) return false
        if (number_of_modules != other.number_of_modules) return false
        if (latency_timing != other.latency_timing) return false
        if (motherboard_form_factor != other.motherboard_form_factor) return false
        if (case_type != other.case_type) return false
        if (number_of_fans != other.number_of_fans) return false
        if (number_of_cores != other.number_of_cores) return false
        if (number_of_thread != other.number_of_thread) return false
        if (has_igpu != other.has_igpu) return false
        if (socket_type != other.socket_type) return false
        if (max_tdp != other.max_tdp) return false
        if (number_of_memory_slots != other.number_of_memory_slots) return false
        if (number_of_m2_slots != other.number_of_m2_slots) return false
        if (brand_compatibility != other.brand_compatibility) return false
        if (number_of_ports != other.number_of_ports) return false
        if (number_of_bays != other.number_of_bays) return false
        if (power != other.power) return false
        if (efficiency_rating != other.efficiency_rating) return false
        if (height != other.height) return false
        if (weight != other.weight) return false
        if (mount_type != other.mount_type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (condition?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + negotiable.hashCode()
        result = 31 * result + exchange_possible.hashCode()
        result = 31 * result + boosted.hashCode()
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (subcategory?.hashCode() ?: 0)
        result = 31 * result + (product_type?.hashCode() ?: 0)
        result = 31 * result + (seller_id?.hashCode() ?: 0)
        result = 31 * result + (seller_phone?.hashCode() ?: 0)
        result = 31 * result + (imageUrls?.hashCode() ?: 0)
        result = 31 * result + (date_posted?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (wishlisted_by?.hashCode() ?: 0)
        result = 31 * result + (brand?.hashCode() ?: 0)
        result = 31 * result + (model?.hashCode() ?: 0)
        result = 31 * result + (system_configuration?.hashCode() ?: 0)
        result = 31 * result + (output_power ?: 0)
        result = 31 * result + (connectivity?.hashCode() ?: 0)
        result = 31 * result + (form_factor?.hashCode() ?: 0)
        result = 31 * result + (polar_pattern?.hashCode() ?: 0)
        result = 31 * result + (subtype?.hashCode() ?: 0)
        result = 31 * result + (number_of_channels?.hashCode() ?: 0)
        result = 31 * result + (amplification_type?.hashCode() ?: 0)
        result = 31 * result + (number_of_pads ?: 0)
        result = 31 * result + (controller_pads?.hashCode() ?: 0)
        result = 31 * result + (range ?: 0)
        result = 31 * result + (drive_system?.hashCode() ?: 0)
        result = 31 * result + (type_of_cartridge?.hashCode() ?: 0)
        result = 31 * result + (connector_1_type?.hashCode() ?: 0)
        result = 31 * result + (connector_2_type?.hashCode() ?: 0)
        result = 31 * result + (keyboard_switches?.hashCode() ?: 0)
        result = 31 * result + (cable_length ?: 0)
        result = 31 * result + (device_interface?.hashCode() ?: 0)
        result = 31 * result + (number_of_buttons ?: 0)
        result = 31 * result + (backlighting?.hashCode() ?: 0)
        result = 31 * result + (color?.hashCode() ?: 0)
        result = 31 * result + (cable_type?.hashCode() ?: 0)
        result = 31 * result + (battery_capacity ?: 0)
        result = 31 * result + (voltage?.hashCode() ?: 0)
        result = 31 * result + (output_energy ?: 0)
        result = 31 * result + (max_dpi ?: 0)
        result = 31 * result + (tracking_method?.hashCode() ?: 0)
        result = 31 * result + (storage_capacity?.hashCode() ?: 0)
        result = 31 * result + (disc_type?.hashCode() ?: 0)
        result = 31 * result + (max_write_speed?.hashCode() ?: 0)
        result = 31 * result + (capture_card_type?.hashCode() ?: 0)
        result = 31 * result + (max_video_resolution?.hashCode() ?: 0)
        result = 31 * result + (max_frame_rate?.hashCode() ?: 0)
        result = 31 * result + (cc_number_of_channels?.hashCode() ?: 0)
        result = 31 * result + (hdr_support?.hashCode() ?: 0)
        result = 31 * result + (supported_cards?.hashCode() ?: 0)
        result = 31 * result + (number_of_slots ?: 0)
        result = 31 * result + (power_demand ?: 0)
        result = 31 * result + (compatibility?.hashCode() ?: 0)
        result = 31 * result + (wireless_charging?.hashCode() ?: 0)
        result = 31 * result + (wifi_speed ?: 0)
        result = 31 * result + (lan_port_speed ?: 0)
        result = 31 * result + (net_technology?.hashCode() ?: 0)
        result = 31 * result + (bluetooth_version?.hashCode() ?: 0)
        result = 31 * result + (read_speed?.hashCode() ?: 0)
        result = 31 * result + (write_speed?.hashCode() ?: 0)
        result = 31 * result + (hard_drive_size?.hashCode() ?: 0)
        result = 31 * result + (input_type?.hashCode() ?: 0)
        result = 31 * result + (output_type?.hashCode() ?: 0)
        result = 31 * result + (laptop_screen_size?.hashCode() ?: 0)
        result = 31 * result + (material?.hashCode() ?: 0)
        result = 31 * result + (closure_type?.hashCode() ?: 0)
        result = 31 * result + (memory_card_type?.hashCode() ?: 0)
        result = 31 * result + (length?.hashCode() ?: 0)
        result = 31 * result + (width?.hashCode() ?: 0)
        result = 31 * result + (thickness ?: 0)
        result = 31 * result + (tip_size ?: 0)
        result = 31 * result + (battery_life ?: 0)
        result = 31 * result + (wifi_standard?.hashCode() ?: 0)
        result = 31 * result + (drive_subtype?.hashCode() ?: 0)
        result = 31 * result + (rotation_speed ?: 0)
        result = 31 * result + (wattage ?: 0)
        result = 31 * result + (number_of_outlets ?: 0)
        result = 31 * result + (ups_power ?: 0)
        result = 31 * result + (usage?.hashCode() ?: 0)
        result = 31 * result + (memory_size?.hashCode() ?: 0)
        result = 31 * result + (memory_type?.hashCode() ?: 0)
        result = 31 * result + (graphic_card_interface?.hashCode() ?: 0)
        result = 31 * result + (max_memory_frequency ?: 0)
        result = 31 * result + (number_of_modules ?: 0)
        result = 31 * result + (latency_timing?.hashCode() ?: 0)
        result = 31 * result + (motherboard_form_factor?.hashCode() ?: 0)
        result = 31 * result + (case_type?.hashCode() ?: 0)
        result = 31 * result + (number_of_fans ?: 0)
        result = 31 * result + (number_of_cores ?: 0)
        result = 31 * result + (number_of_thread ?: 0)
        result = 31 * result + (has_igpu?.hashCode() ?: 0)
        result = 31 * result + (socket_type?.hashCode() ?: 0)
        result = 31 * result + (max_tdp ?: 0)
        result = 31 * result + (number_of_memory_slots ?: 0)
        result = 31 * result + (number_of_m2_slots ?: 0)
        result = 31 * result + (brand_compatibility ?: 0)
        result = 31 * result + (number_of_ports ?: 0)
        result = 31 * result + (number_of_bays ?: 0)
        result = 31 * result + (power ?: 0)
        result = 31 * result + (efficiency_rating?.hashCode() ?: 0)
        result = 31 * result + (height?.hashCode() ?: 0)
        result = 31 * result + (weight ?: 0)
        result = 31 * result + (mount_type?.hashCode() ?: 0)
        return result
    }
}