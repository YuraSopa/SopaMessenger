package com.example.sopamessenger.presentation.chat_screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sopamessenger.R
import com.example.sopamessenger.data.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ChatScreen(
    navController: NavController,
    channelId: String,
    channelName: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    Scaffold { innerPadding ->

        val chooserDialogState = remember {
            mutableStateOf(false)
        }

        val cameraImageUri = remember {
            mutableStateOf<Uri?>(null)
        }


        val cameraImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                cameraImageUri.value?.let {
                    //send image to server
                    viewModel.sendImageMessage(it, channelId)
                }
            }
        }

        val imageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { viewModel.sendImageMessage(it, channelId) }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                cameraImageLauncher.launch(
                    viewModel.createImageUri(navController.context, cameraImageUri)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFCEDEFF))
        ) {
            LaunchedEffect(key1 = true) {
                viewModel.listenForMessages(channelId)
            }
            val messages = viewModel.messages.collectAsState()
            ChatMessages(
                channelId = channelId,
                messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelId, message)
                },
                onImageClicked = {
                    chooserDialogState.value = true
                })
        }





        if (chooserDialogState.value) {
            ContentSelectionDialog(onCameraSelected = {
                chooserDialogState.value = false
                if (navController.context.checkSelfPermission(Manifest.permission.CAMERA)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    cameraImageLauncher.launch(
                        viewModel.createImageUri(navController.context, cameraImageUri)
                    )
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }, onGallerySelected = {
                chooserDialogState.value = false
                imageLauncher.launch("image/*")
            })

        }
    }
}

@Composable
fun ChatMessages(
    channelId: String,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onImageClicked: () -> Unit
) {
    val listState = rememberLazyListState()
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(index = messages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.weight(1f), state = listState) {
            items(messages) { message ->
                ChatMessage(channelId, message)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = msg.value,
                onValueChange = { msg.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = "Message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Black,
                    unfocusedPlaceholderColor = Color.Black
                )
            )
            IconButton(
                onClick = {
                    msg.value = ""
                    onImageClicked()
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.attach_file),
                    contentDescription = "attach"
                )
            }
            IconButton(
                onClick = {
                    if (msg.value.trim().isNotEmpty()) {
                        onSendMessage(msg.value)
                    }
                    msg.value = ""
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "send"
                )
            }
        }
    }
}

@Composable
fun ChatMessage(channelId: String, message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid

    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    var showImageDialog by remember { mutableStateOf(false) }
    var showImageMenuState = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .align(alignment)
        ) {
            if (!isCurrentUser) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .align(Alignment.Bottom),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_placeholder),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            val messageMod = if (!isCurrentUser) {
                Modifier
                    .padding(end = 40.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            } else {
                Modifier
                    .padding(start = 40.dp)
                    .background(color = Color.Green, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            }

            Box(modifier = messageMod.clickable {
                showImageMenuState.value = true
            }) {
                if (message.imageUrl != null) {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(250.dp)
                            .clickable {
                                showImageDialog = true
                            }
                    )

                    if (showImageDialog) {
                        ImageDialogFullscreen(
                            onDismissRequest = { showImageDialog = false },
                            onImageClicked = { showImageDialog = true },
                            message = message
                        )
                    }

                } else {
                    Text(
                        text = message.message ?: "",
                        softWrap = true,
                        overflow = TextOverflow.Clip,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                MessageDropdownMenu(
                    showImageMenu = showImageMenuState,
                    context = context,
                    channelId = channelId,
                    message = message
                )
            }
        }
    }
}

@Composable
fun ContentSelectionDialog(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    AlertDialog(
        confirmButton = {
            TextButton(onClick = onCameraSelected) {
                Text(text = "Camera", color = MaterialTheme.colorScheme.primary)
            }
        },
        onDismissRequest = { },
        dismissButton = {
            TextButton(onClick = onGallerySelected) {
                Text(text = "Gallery", color = MaterialTheme.colorScheme.primary)
            }
        },
        title = { Text(text = "Select your choice") },
        text = { Text(text = "Would you like to pick an image from the gallery or use the camera") })
}

@Composable
fun ImageDialogFullscreen(
    onDismissRequest: () -> Unit,
    onImageClicked: () -> Unit,
    message: Message
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = message.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClicked() }
            )
        }
    }
}

@Composable
fun MessageDropdownMenu(
    showImageMenu: MutableState<Boolean>,
    context: Context,
    channelId: String,
    message: Message,
    viewModel: ChatViewModel = hiltViewModel()
) {
    DropdownMenu(
        expanded = showImageMenu.value,
        onDismissRequest = { showImageMenu.value = false }) {
        DropdownMenuItem(
            text = { Text(text = "Copy") },
            onClick = {
                viewModel.copyToClipboard(
                    context,
                    message.message ?: ""
                )
                showImageMenu.value = false
            })
        DropdownMenuItem(
            text = { Text(text = "Delete") },
            onClick = {
                viewModel.deleteMessage(channelId, message.id)
                showImageMenu.value = false
            })
    }
}