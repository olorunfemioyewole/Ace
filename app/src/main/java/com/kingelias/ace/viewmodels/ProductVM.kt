package com.kingelias.ace.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kingelias.ace.data.Category
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.utils.Constants

class ProductVM: ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    //database nodes
    private val database = FirebaseDatabase.getInstance()
    private val dbProducts = database.reference.child(Constants.NODE_PRODUCTS)
    private val dbCategories = database.reference.child(Constants.NODE_CATEGORY)
    private val dbSubCategories = database.reference.child(Constants.NODE_SUBCATEGORY)

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

    private var _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    val _ready = MutableLiveData<Boolean>()
    val ready: LiveData<Boolean>
        get() = _ready

    fun fetchCategories(){
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
                _ready.value = false
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
}