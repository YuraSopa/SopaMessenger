package com.example.sopamessenger.presentation.chat_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {

    val channels = viewModel.channels.collectAsState()

    channels.value.forEach { channel ->
        Timber.d("Channel: ${channel.name}")
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(channels.value) { channel ->
                Column {
                    Text(
                        text = "${channel.id} ${channel.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable {
                                Timber.d("Click on ${channel.name}")
                            }
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}