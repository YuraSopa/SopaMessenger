package com.example.sopamessenger.presentation.chat_screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.sopamessenger.data.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel(

) {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    private val db = Firebase.database

    fun sendMessage(channelId: String, messageText: String?, image: String? = null) {
        val messageId =
            db.reference.child("messages").child(channelId).push().key ?: UUID.randomUUID().toString()
        val message = Message(
            messageId,
            Firebase.auth.currentUser?.uid ?: "",
            messageText,
            System.currentTimeMillis(),
            Firebase.auth.currentUser?.displayName ?: "",
            null,
            image
        )
        db.reference.child("messages").child(channelId).child(messageId).setValue(message)
    }


    fun listenForMessages(channelId: String) {
        db.getReference("messages").child(channelId).orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let { list.add(it) }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun createImageUri(context: Context, cameraImageUri: MutableState<Uri?>): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir =
            ContextCompat.getExternalFilesDirs(context, DIRECTORY_PICTURES)
                .first()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File.createTempFile("IMAGE_$timeStamp", ".jpg", storageDir).apply {
                cameraImageUri.value = Uri.fromFile(this)
            }
        )
    }

    fun sendImageMessage(uri: Uri, channelId: String) {
        val imageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val currentUser = Firebase.auth.currentUser
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    sendMessage(channelId, null, downloadUri.toString())
                }

            }
    }

    fun deleteMessage(channelId: String, messageId: String) {
        Timber.d("Deleting message with ID: $messageId in channel: $channelId")
        db.reference.child("messages").child(channelId).child(messageId).removeValue()
            .addOnSuccessListener {
                Timber.d("Message deleted successfully")
            }
            .addOnFailureListener { exception ->
                Timber.e("Error deleting message: $exception")
            }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Message", text)
        clipboard.setPrimaryClip(clip)
    }
}