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
import com.kingelias.ace.data.User
import com.kingelias.ace.utils.Constants.NODE_PROFILE_PIC
import com.kingelias.ace.utils.Constants.NODE_USERS

class UserVM: ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    //database nodes
    private val database = FirebaseDatabase.getInstance()
    private val dbUsers = database.reference.child(NODE_USERS)

    //storage nodes
    private val storage = FirebaseStorage.getInstance()
    private val cstProfilePicRef = storage.reference.child(NODE_PROFILE_PIC)

    lateinit var userToEdit: User
    var businessDetails: Boolean = false

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _seller = MutableLiveData<User>()
    val seller: LiveData<User>
        get() = _seller

    private val _userImg = MutableLiveData<Uri>()
    val userImg: LiveData<Uri>
        get() = _userImg

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    val _ready = MutableLiveData<Boolean>()
    val ready: LiveData<Boolean>
        get() = _ready

    private val _imgUploadComplete = MutableLiveData<Boolean>()
    val imgUploadComplete: LiveData<Boolean>
        get() = _imgUploadComplete

    fun addUser(user: User){
        dbUsers.child(user.id!!).setValue(user)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    _result.value = null
                }
                else{
                    _result.value = it.exception
                }
            }
    }

    fun uploadProfilePicture(selectedImage: Uri, userID: String) {
        val fileName = "${userID}.jpg"
        val imageRef = cstProfilePicRef.child(fileName)
        var imageUrl: String

        val uploadTask = imageRef.putFile(selectedImage)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                imageUrl = uri.toString()
                dbUsers.child(userID).child(NODE_PROFILE_PIC).setValue(imageUrl)
                    .addOnSuccessListener {
                        _imgUploadComplete.value =  true
                    }
            }
        }.addOnFailureListener {
            _result.value = it
        }

    }

    fun changePfpUrl(photoUrl: String, userID: String) {
        dbUsers.child(userID).child(NODE_PROFILE_PIC).setValue(photoUrl)
            .addOnSuccessListener {
                _imgUploadComplete.value =  true
            }.addOnFailureListener {
                _result.value = it
            }

    }

    fun updateUserField(userID: String, fieldName: String, value: Any) {
        dbUsers.child(userID).child(fieldName).setValue(value)
            .addOnSuccessListener {
                _result.value = null
            }
            .addOnFailureListener{
                _result.value = it
            }
    }

    fun getUser(){
        auth.currentUser?.let {
            dbUsers.child(it.uid).addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user = snapshot.getValue(User::class.java)
                        user?.id = snapshot.key

                        val wishlist = mutableListOf<String>()
                        for (item in snapshot.child("wishlisted").children){
                            wishlist.add(item.key.toString())
                        }
                        user?.wishlist = wishlist

                        _user.value = user!!
                        _ready.value = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun getSeller(userID: String){
        dbUsers.child(userID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user?.id = snapshot.key
                    _seller.value = user!!
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteUser() {
        val userID = auth.currentUser?.uid
        auth.currentUser?.delete()
            ?.addOnSuccessListener {
                FirebaseAuth.getInstance().signOut()
                if (userID != null) {
                    dbUsers.child(userID).setValue(null)
                        .addOnSuccessListener {
                            cstProfilePicRef.child("$userID.jpg").delete()
                        }
                        .addOnFailureListener{
                            _result.value = it
                        }
                }
            }
            ?.addOnFailureListener{
                _result.value = it
            }
    }

}