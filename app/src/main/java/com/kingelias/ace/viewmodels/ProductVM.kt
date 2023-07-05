package com.kingelias.ace.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.kingelias.ace.data.Category
import com.kingelias.ace.data.Product
import com.kingelias.ace.data.ProductType
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.utils.Constants
import java.nio.file.Files.delete

class ProductVM: ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    //database nodes
    private val database = FirebaseDatabase.getInstance()
    private val dbProducts = database.reference.child(Constants.NODE_PRODUCTS)
    private val dbCategories = database.reference.child(Constants.NODE_CATEGORY)
    private val dbSubCategories = database.reference.child(Constants.NODE_SUBCATEGORY)
    private val dbProductTypes = database.reference.child(Constants.NODE_PRODUCT_TYPES)

    //storage nodes
    private val storage = FirebaseStorage.getInstance()
    private val cstProductImage = storage.reference.child(Constants.NODE_PRODUCT_IMAGE)

    private var _searchResult = MutableLiveData<List<Product>>()
    val searchResult: LiveData<List<Product>>
        get() = _searchResult

    private var _activeAds = MutableLiveData<List<Product>>()
    val activeAds: LiveData<List<Product>>
        get() = _activeAds
    private var _pendingAds = MutableLiveData<List<Product>>()
    val pendingAds: LiveData<List<Product>>
        get() = _pendingAds
    private var _declinedAds = MutableLiveData<List<Product>>()
    val declinedAds: LiveData<List<Product>>
        get() = _declinedAds
    private var _draftAds = MutableLiveData<List<Product>>()
    val draftAds: LiveData<List<Product>>
        get() = _draftAds

    private var _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    private var _phoneSubcategories = MutableLiveData<List<Subcategory>>()
    val phoneSubcategories: LiveData<List<Subcategory>>
        get() = _phoneSubcategories
    private var _electronicsSubcategories = MutableLiveData<List<Subcategory>>()
    val electronicsSubcategories: LiveData<List<Subcategory>>
        get() = _electronicsSubcategories
    private var _fashionSubcategories = MutableLiveData<List<Subcategory>>()
    val fashionSubcategories: LiveData<List<Subcategory>>
        get() = _fashionSubcategories

    private var _productTypes = MutableLiveData<List<ProductType>>()
    val productTypes: LiveData<List<ProductType>>
        get() = _productTypes

    private val _imgUploadComplete = MutableLiveData<Boolean>()
    val imgUploadComplete: LiveData<Boolean>
        get() = _imgUploadComplete

    private var _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    val _ready = MutableLiveData<Boolean>()
    val ready: LiveData<Boolean>
        get() = _ready

    lateinit var currentProduct: Product
    var editProduct: Boolean = false

    var boostedPlan: Boolean = false
    var selectedImages= mutableListOf<Uri>()

    lateinit var selectedCategory: Category
    lateinit var selectedSubcategory: String
    lateinit var selectedSubcatTypes: List<String>
    lateinit var selectedProductType: String
    lateinit var newAd: Product
    lateinit var adToEdit: Product

    fun fetchVendorProducts(userId: String){
        dbProducts.orderByChild("seller_id").equalTo(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val productList = mutableListOf<Product>()

                    for (productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.id = productSnapshot.key

                        val imageUrls = productSnapshot.child("image").children.mapNotNull { it.getValue(String::class.java) }
                        product?.imageUrls = imageUrls

                        product?.let {productList.add(it)}
                    }

                    _searchResult.value = productList
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchCategories(){
        dbCategories.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _ready.value = false

                if (snapshot.exists()){
                    val categories = mutableListOf(Category("Category*"))

                    for (feedbackSnapshot in snapshot.children){
                        val category = feedbackSnapshot.getValue(Category::class.java)
                        category?.name = feedbackSnapshot.key

                        if (category?.name != "Trending"){
                            category?.let {categories.add(it)}
                        }
                    }

                    _categories.value = categories
                    _ready.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchHomeCategories(){
        dbCategories.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _ready.value = false

                if (snapshot.exists()){
                    val categories = mutableListOf<Category>()

                    for (feedbackSnapshot in snapshot.children){
                        val category = feedbackSnapshot.getValue(Category::class.java)
                        category?.name = feedbackSnapshot.key

                        category?.let {categories.add(it)}

                    }

                    _categories.value = categories
                    _ready.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchSubCategories(){
        dbSubCategories.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val fashionSubcategories = mutableListOf<Subcategory>()
                    val electronicsSubcategories = mutableListOf<Subcategory>()
                    val phoneSubcategories = mutableListOf<Subcategory>()

                    for (feedbackSnapshot in snapshot.children){
                        val subcategory = feedbackSnapshot.getValue(Subcategory::class.java)
                        subcategory?.name = feedbackSnapshot.key

                        if (subcategory != null) {
                            when(subcategory.category){
                                "Fashion" -> {fashionSubcategories.add(subcategory)}
                                "Electronics" -> {electronicsSubcategories.add(subcategory)}
                                "Phones and Tablets" -> {phoneSubcategories.add(subcategory)}
                            }
                        }
                    }

                    _fashionSubcategories.value = fashionSubcategories
                    _electronicsSubcategories.value = electronicsSubcategories
                    _phoneSubcategories.value = phoneSubcategories
                    _ready.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchNewAdSubCategories(){
        dbSubCategories.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val fashionSubcategories = mutableListOf<Subcategory>(Subcategory("Subcategory*"))
                    val electronicsSubcategories = mutableListOf<Subcategory>(Subcategory("Subcategory*"))
                    val phoneSubcategories = mutableListOf<Subcategory>(Subcategory("Subcategory*"))

                    for (feedbackSnapshot in snapshot.children){
                        val subcategory = feedbackSnapshot.getValue(Subcategory::class.java)
                        subcategory?.name = feedbackSnapshot.key

                        if (subcategory != null) {
                            when(subcategory.category){
                                "Fashion" -> {fashionSubcategories.add(subcategory)}
                                "Electronics" -> {electronicsSubcategories.add(subcategory)}
                                "Phones and Tablets" -> {phoneSubcategories.add(subcategory)}
                            }
                        }
                    }

                    _fashionSubcategories.value = fashionSubcategories
                    _electronicsSubcategories.value = electronicsSubcategories
                    _phoneSubcategories.value = phoneSubcategories
                    _ready.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun performSearch(query: String) {
        val searchQuery = query.lowercase()

        dbProducts.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val searchResults = mutableListOf<Product>()

                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.id = productSnapshot.key
                        val imageUrls = productSnapshot.child("image").children.mapNotNull { it.getValue(String::class.java) }
                        product?.imageUrls = imageUrls


                        if (product != null && product.status == "Active") {
                            val productName = product.title!!.lowercase()
                            val productType = product.product_type!!.lowercase()
                            val productCat = product.category!!.lowercase()

                            if (productName.contains(searchQuery) || productType.contains(searchQuery) || productCat.contains(searchQuery)) {
                                if (product.seller_id != auth.currentUser?.uid.toString()){
                                    searchResults.add(product)
                                }
                            }
                        }
                    }

                    _searchResult.value = searchResults
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _result.value = Exception(error.message)
            }
        })

    }

    fun performSearchBySubCat(query: String) {
        dbProducts.orderByChild("subcategory").equalTo(query).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val productList = mutableListOf<Product>()

                    for (productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.id = productSnapshot.key

                        val imageUrls = productSnapshot.child("image").children.mapNotNull { it.getValue(String::class.java) }
                        product?.imageUrls = imageUrls

                        if (product != null && product.status == "Active") {
                            productList.add(product)
                        }
                    }

                    _searchResult.value = productList
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchMyAds() {
        val userId = auth.currentUser?.uid

        dbProducts.orderByChild("seller_id").equalTo(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val activeProductList = mutableListOf<Product>()
                    val pendingProductList = mutableListOf<Product>()
                    val declinedProductList = mutableListOf<Product>()
                    val draftProductList = mutableListOf<Product>()

                    for (productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.id = productSnapshot.key

                        val imageUrls = productSnapshot.child("image").children.mapNotNull { it.getValue(String::class.java) }
                        product?.imageUrls = imageUrls

                        if (product != null && product.status == "Active") {
                            activeProductList.add(product)
                        } else if (product != null && product.status == "Pending") {
                            pendingProductList.add(product)
                        } else if (product != null && product.status == "Declined") {
                            declinedProductList.add(product)
                        } else if (product != null && product.status == "Drafts") {
                            draftProductList.add(product)
                        }
                    }

                    _activeAds.value = activeProductList
                    _pendingAds.value = pendingProductList
                    _declinedAds.value = declinedProductList
                    _draftAds.value = draftProductList
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchWishlist(wishlistIds: List<String>) {
        val productList = mutableListOf<Product>()

        for (id in wishlistIds){
            dbProducts.child(id).addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val product = snapshot.getValue(Product::class.java)
                        product?.id = snapshot.key

                        val imageUrls = snapshot.child("image").children.mapNotNull { it.getValue(String::class.java) }
                        product?.imageUrls = imageUrls

                        productList.add(product!!)
                        _searchResult.value = productList
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    }

    fun fetchTypes() {
        dbProductTypes.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val productTypesList = mutableListOf<ProductType>()

                    for (typeSnapshot in snapshot.children){
                        val productTypes = typeSnapshot.getValue(ProductType::class.java)
                        productTypes?.id = typeSnapshot.key

                        if (productTypes != null) {
                            productTypesList.add(productTypes)
                        }
                    }

                    _productTypes.value = productTypesList
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun uploadNewAd() {
        newAd.id = dbProducts.push().key

        dbProducts.child(newAd.id.toString()).setValue(newAd)
            .addOnSuccessListener {
                uploadNewAdPictures()
            }
            .addOnFailureListener{
                _result.value = it
            }
    }

    fun deleteAd(id: String) {
        dbProducts.child(id).setValue(null)
            .addOnSuccessListener {
                deleteAdPictures(id)
                _imgUploadComplete.value = true
            }
            .addOnFailureListener{
                _result.value = it
            }
    }

    private fun deleteAdPictures(id: String) {
        val productFolderRef = cstProductImage.child(id)
        productFolderRef.listAll().addOnSuccessListener { result ->
            for (fileRef in result.items) {
                fileRef.delete()
            }
            productFolderRef.delete().addOnSuccessListener {
                _imgUploadComplete.value = true
            }.addOnFailureListener { exception ->
                _result.value = exception
            }
        }.addOnFailureListener { exception ->
            _result.value = exception
        }
    }

    private fun uploadNewAdPictures() {
        for (i in selectedImages.indices){
            val fileName = "${newAd.id} $i.jpg"
            val imageRef = cstProductImage.child(newAd.id.toString()).child(fileName)
            var imageUrl: String

            val uploadTask = imageRef.putFile(selectedImages[i])
            uploadTask.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    dbProducts.child(newAd.id.toString()).child("image").child(i.toString()).setValue(imageUrl)
                        .addOnSuccessListener {
                            _imgUploadComplete.value =  true
                        }
                }
            }.addOnFailureListener {
                _result.value = it
            }
        }

    }

}