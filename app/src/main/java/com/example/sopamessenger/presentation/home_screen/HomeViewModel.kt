package com.example.sopamessenger.presentation.home_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sopamessenger.data.model.Channel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val firebaseDatabase = Firebase.database
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()
    val addChannelState = mutableStateOf(false)

    init {
        getChannels()
    }

    private fun getChannels() {
        Timber.d("TEST viewMODEL")
        firebaseDatabase.getReference("channel").get().addOnSuccessListener {
            val list = mutableListOf<Channel>()
            it.children.forEach { data ->
                val channel = Channel(data.key!!, data.value.toString())
                list.add(channel)
            }
            _channels.value = list
        }
    }


    fun addChannel(name: String) {
        val key = firebaseDatabase.getReference("channel").push().key
        key?.let {
            firebaseDatabase.getReference("channel").child(it).setValue(name).addOnSuccessListener {
                getChannels()
            }
        }
    }
}