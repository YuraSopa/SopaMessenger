package com.example.sopamessenger.data

data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String? = "",
    val timestamp: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderImage: String? = null,
    val imageUrl: String? = null
)
