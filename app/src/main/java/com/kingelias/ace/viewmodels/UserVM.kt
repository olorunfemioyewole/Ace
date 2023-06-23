package com.kingelias.ace.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kingelias.ace.data.User
import com.kingelias.ace.utils.Constants.NODE_PROFILE_PIC
import com.kingelias.ace.utils.Constants.NODE_USERS

class UserVM: ViewModel() {
    val auth = FirebaseAuth.getInstance()

    //database nodes
    private val database = FirebaseDatabase.getInstance()
    private val dbUsers = database.reference.child(NODE_USERS)

    //storage nodes
    private val storage = FirebaseStorage.getInstance()
    private val cstProfilePicRef = storage.reference.child(NODE_PROFILE_PIC)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userImg = MutableLiveData<Uri>()
    val userImg: LiveData<Uri>
        get() = _userImg

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

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
        var imageUrl = ""

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

}