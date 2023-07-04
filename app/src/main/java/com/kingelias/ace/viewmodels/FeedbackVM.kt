package com.kingelias.ace.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kingelias.ace.data.Feedback
import com.kingelias.ace.data.User
import com.kingelias.ace.utils.Constants

class FeedbackVM: ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    //database nodes
    private val database = FirebaseDatabase.getInstance()
    private val dbFeedback = database.reference.child(Constants.NODE_FEEDBACK)

    lateinit var newFeedback: Feedback
    lateinit var sender: User
    lateinit var receipient: String

    private val _feedbackSent = MutableLiveData<Boolean>()
    val feedbackSent: LiveData<Boolean>
        get() = _feedbackSent

    private val _feedback = MutableLiveData<List<Feedback>>()
    val feedback: LiveData<List<Feedback>>
        get() = _feedback

    private var _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    fun fetchMyFeedback(userId: String){
        dbFeedback.orderByChild("receiver_id").equalTo(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val feedbackList = mutableListOf<Feedback>()

                    for (feedbackSnapshot in snapshot.children){
                        val feedbackItem = feedbackSnapshot.getValue(Feedback::class.java)
                        feedbackItem?.id = feedbackSnapshot.key

                        feedbackItem?.let {feedbackList.add(it)}
                    }

                    _feedback.value = feedbackList
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendFeedback() {
        newFeedback.id = dbFeedback.push().key

        dbFeedback.child(newFeedback.id.toString()).setValue(newFeedback)
            .addOnSuccessListener {
                _feedbackSent.value = true
            }
    }
}