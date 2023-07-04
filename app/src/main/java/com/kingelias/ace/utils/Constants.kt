package com.kingelias.ace.utils

import com.kingelias.ace.R
import com.kingelias.ace.data.OnboardItem

object Constants {
    //firebase nodes
    const val NODE_USERS = "users"
    const val NODE_FEEDBACK = "feedback"
    const val NODE_PROFILE_PIC = "profile_pic"
    const val NODE_PRODUCTS = "product"
    const val NODE_CATEGORY = "category"
    const val NODE_SUBCATEGORY = "subcategory"
    const val NODE_WISHLIST = "wishlisted"
    const val NODE_WISHLISTERS = "wishlisters"
    const val NODE_PRODUCT_TYPES = "product_type"
    const val NODE_PRODUCT = "product"
    const val NODE_PRODUCT_IMAGE = "product_image"

    val onboardItems: List<OnboardItem> = listOf(
        OnboardItem("Discover", "A wide range of products tailored to your needs!", R.drawable.onboard_1),
        OnboardItem("Effortlessly", "Find exactly what you're looking for", R.drawable.onboard_2),
        OnboardItem("Wishlist", "Save your favorite items for later", R.drawable.onboard_3)
    )

    val genders: List<String> = listOf(
        "Gender*",
        "Male",
        "Female",
        "Prefer not to say"
    )

    val condition: List<String> = listOf(
        "Condition*",
        "Any",
        "Brand New",
        "Used"
    )

    val dealOutcomes: List<String> = listOf(
        "How did it go?*",
        "Successful Purchase",
        "The deal failed",
        "Couldn't agree on price",
        "Couldn't reach the seller"
    )

    val businessTypes: List<String> = listOf(
        "Business Type",
        "Online Vendor",
        "Physical Store"
    )

    val regions: List<String> = listOf(
        "Location*",
        "Greater Accra Region",
        "Central Region",
        "Ashanti Region",
        "Western Region",
        "Eastern Region",
        "Volta Region",
        "Brong-Ahafo Region",
        "Northern Region",
        "Upper West Region",
        "Upper East Region"
    )

    val formFactor: List<String> = listOf(
        "Form Factor",
        "Condenser",
        "Desktop Microphone",
        "Dynamic",
        "Headset",
        "Lavalier/Lapel",
        "Wireless",
        "Other"
    )

    val polarPattern: List<String> = listOf(
        "Polar Pattern",
        "Bidirectional",
        "Cardiod",
        "Hypercardioid",
        "Omnidirectional",
        "Shotgun",
        "Supercardioid",
        "Unidirectional",
        "Other",
    )

    val gameOperatingSystems: List<String> = listOf(
        "Operating System",
        "Sony Playstation 4",
        "Sony Playstation 3",
        "Sony Playstation 2",
        "Sony Playstation",
        "Xbox Series X",
        "Xbox Series S",
        "Xbox One",
        "Xbox 360",
        "Windows 11",
        "Windows 10",
        "Windows 8.1",
        "Windows 8",
        "Windows 7",
        "Linux",
        "Mac OS",
    )

    val operatingSystems: List<String> = listOf(
        "Operating System",
        "Android 13",
        "Android 12",
        "Android 11",
        "Android 10",
        "Android 9",
        "Android 8",
        "Android 7",
        "Android 6",
        "Blackberry OS",
        "Chrome OS",
        "iOS",
        "iPad OS",
        "Windows 11",
        "Windows 10",
        "Windows 8.1",
        "Windows 8",
        "Windows 7",
        "Windows Vista",
        "Windows XP",
        "Windows Phone",
        "Wear OS",
        "Linux",
        "Mac OS",
        "Ubuntu",
        "Free DOS",
        "DOS",
        "Non OS",
    )

    val subtype: List<String> = listOf(
        "Subtype",
        "Any",
        "Analog",
        "Digital"
    )

    val amplificationType: List<String> = listOf(
        "Amplification Type",
        "Any",
        "Active",
        "Passive"
    )

    val keyboardSwitches: List<String> = listOf(
        "Keyboard Switches",
        "Mechanical",
        "Membrane",
        "Mixed",
        "Optical",
        "Other",
    )

    val config: List<String> = listOf(
        "System Configuration",
        "1.0 Channel",
        "2.0 Channel",
        "2.1 Channel",
        "2.2 Channel",
        "2.4 Channel",
        "3.0 Channel",
        "3.1 Channel",
        "4.0 Channel",
        "4.1 Channel",
        "5.0 Channel",
        "5.1 Channel",
        "5.1.2 Channel",
        "6-Channel",
        "7.2 Channel",
        "9 Channel",
        "Other",
    )

    val cableType: List<String> = listOf(
        "Cable Type",
        "HDMI",
        "HDMI - HDMI",
        "VGA",
        "USB",
        "DisplayPort",
        "RCA",
        "3.5mm - 3.5mm",
        "3.5mm - 2.5mm",
        "3.5mm - RCA",
        "3.5mm - 6.3mm",
        "3.5mm - 2xRCA",
        "3.5mm - USB",
        "Apple Lightning - 3.5mm",
        "Apple Lightning - Apple Lightning",
        "Apple Lightning - Apple Lightning + 3.5mm",
        "Apple Lightning - microUSB",
        "Apple Lightning - USB type C",
        "AUX - AUX",
        "BNC",
        "C13 - C14",
        "COM",
        "DVI",
        "DVI - DVI",
        "DVI - VGA",
        "Ethernet",
        "Firewire",
        "HDMI - DVI",
        "HDMI - RCA",
        "HDMI - USB",
        "HDMI - VGA",
        "HDMI - VGA + 3.5mm",
        "IEC - IEC",
        "Jack 3.5mm",
        "Lightning USB",
        "Micro HDMI - HDMI",
        "Micro USB",
        "Micro USB - Type C",
        "Micro USB - USB",
        "mini DisplayPort - DisplayPort",
        "mini DisplayPort - HDMI",
        "mini HDMI - HDMI",
        "RCA",
        "SATA",
        "SATA - Molex",
        "SATA data",
        "SCART",
        "Schuko",
        "Toslink",
        "Type C - Type  C",
        "USB - Apple 30-pin",
        "USB - Apple Lightning",
        "USB - Apple Lightning - microUSB - USB type C",
        "USB - microUSB",
        "USB - microUSB - Lightning - Type C",
        "USB - microUSB + Apple Lightning",
        "USB - miniUSB",
        "USB - RJ-45",
        "USB 2.0",
        "USB type A - USB type A",
        "USB type A - USB type B",
        "USB type C - 3.5mm",
        "USB type C - HDMI",
        "USB type C - Lightning",
        "USB type C - microUSB",
        "USB type C - USB",
        "USB type C - USB type A",
        "USB type C - USB type C",
        "USB type C",
        "Other"
    )

    val aspectRatio: List<String> = listOf(
        "Aspect Ratio",
        "Any",
        "16:10",
        "16:9",
        "21:9",
        "4:3",
        "5:4"
    )

    val resolution: List<String> = listOf(
        "Aspect Ratio",
        "Any",
        "Ultra HD 3840 x 2160",
        "Quad HD 2560 x 1440",
        "Full HD 1920 x 1080",
        "1920 x 1200",
        "HD 1280 x 700",
        "1600 x 900",
        "8k 7680 x 4320",
        "800 x 6004",
        "6K 6016 x 3384",
        "5K 5120 x 2880",
        "3840 x 1600",
        "3840 x 1200",
        "3840 x 1080",
        "3840 x 1440",
        "2560 x 1600",
        "2560 x 1080",
        "1440 x 900",
        "1280 x 1024",
    )

    val refreshRate: List<String> = listOf(
        "Refresh rate",
        "Any",
        "60 Hz",
        "50-60 Hz",
        "70-85 Hz",
        "100 Hz",
        "120 Hz",
        "144 Hz",
        "165 Hz",
        "240 Hz",
        "360 Hz",
        "480 Hz"
    )

    val displayTech: List<String> = listOf(
        "Display Technology",
        "Any",
        "CRT",
        "IPS",
        "LCD",
        "OLED",
        "TN",
        "VA",
        "QD OLED",
        "mini OLED",
        "Retina",
    )

    val channelNumber: List<String> = listOf(
        "Number of Channels",
        "Any",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "16",
        "32",
        "64",
        "128",
    )

    val connector1Type: List<String> = listOf(
        "Connector 1 Type",
        "USB type C",
        "USB",
        "HDMI",
        "VGA",
        "DisplayPort",
        "3.5 mm",
        "6.3 mm",
        "Apple 30-pin",
        "Apple Lightning",
        "ATX (4-pin)",
        "ATX (6-pin)",
        "Coax",
        "DVI",
        "eSATA",
        "MHL",
        "microHDMI",
        "microUSB",
        "miniDisplayPort",
        "miniDVI",
        "miniHDMI",
        "Molex",
        "RCA",
        "RJ-45",
        "Samsung 30-pin",
        "SATA",
        "SCART",
        "Thunderbolt",
        "USB type A",
        "Other"
    )

    val connector2Type: List<String> = listOf(
        "Connector 2 Type",
        "USB type C",
        "USB",
        "HDMI",
        "VGA",
        "DisplayPort",
        "3.5 mm",
        "6.3 mm",
        "Apple 30-pin",
        "Apple Lightning",
        "ATX (4-pin)",
        "ATX (6-pin)",
        "Coax",
        "DVI",
        "eSATA",
        "MHL",
        "microHDMI",
        "microUSB",
        "miniDisplayPort",
        "miniDVI",
        "miniHDMI",
        "Molex",
        "RCA",
        "RJ-45",
        "Samsung 30-pin",
        "SATA",
        "SCART",
        "Thunderbolt",
        "USB type A",
        "Other"
    )

    //request codes
    const val CAMERA = 100
    const val GALLERY = 200
    const val USER_IMAGE_DIRECTORY = "ProfilePic"
}