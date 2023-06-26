package com.kingelias.ace.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kingelias.ace.data.User
import com.kingelias.ace.utils.Constants

class FeedbackVM: ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    //database nodes
    private val database = FirebaseDatabase.getInstance()
    private val dbFeedback = database.reference.child(Constants.NODE_FEEDBACK)

    private val _feedback = MutableLiveData<User>()
    val feedback: LiveData<User>
        get() = _feedback

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result
}