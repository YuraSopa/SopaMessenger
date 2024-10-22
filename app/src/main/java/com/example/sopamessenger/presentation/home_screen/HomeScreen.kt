package com.example.sopamessenger.presentation.home_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sopamessenger.AppBar
import com.example.sopamessenger.presentation.DrawerHeader
import com.example.sopamessenger.presentation.itemsDrawerMenu
import com.example.sopamessenger.util.getContrastingColor
import com.example.sopamessenger.util.getRandomColor
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val channels = viewModel.channels.collectAsState()
    val addChannelState = remember { viewModel.addChannelState }
    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val selectedItem = remember { mutableStateOf(itemsDrawerMenu[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                itemsDrawerMenu.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.contentDescription
                            )
                        },
                        label = { Text(text = item.title) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                            Toast.makeText(
                                navController.context,
                                item.title,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Scaffold(
                floatingActionButton = {
                    AddChannelFab()
                },
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    AppBar(
                        onNavigationIconClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        onSearchIconClick = {

                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    channels.value.forEach { channel ->
                        Timber.d("Channel: ${channel.name}")
                    }
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn {
                            items(channels.value) { channel ->
                                Column {
                                    ChannelItem(channelName = channel.name) {
                                        navController.navigate("chat/${channel.id}&${channel.name}")
                                    }
                                }
                            }
                        }

                        if (addChannelState.value) {
                            ModalBottomSheet(
                                onDismissRequest = { addChannelState.value = false },
                                sheetState = sheetState
                            ) {
                                AddChannelDialog {
                                    viewModel.addChannel(it)
                                    addChannelState.value = false
                                }
                            }
                        }
                    }
                }
            }

        }
    )

}


@Composable
fun AddChannelFab(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val addChannelState = remember { viewModel.addChannelState }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                addChannelState.value = true
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            modifier = Modifier.size(32.dp),
            contentDescription = "Add channel",
            tint = Color.White
        )
    }
}


@Composable
fun ChannelItem(channelName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.onPrimary)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        val bgColor = remember {
            getRandomColor()
        }
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channelName[0].toString().uppercase(),
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(fontSize = 30.sp),
                color = getContrastingColor(bgColor)
            )
        }
        Text(text = channelName)
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun AddChannelDialog(
    onAddChannel: (String) -> Unit
) {
    val channelName = remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add New Channel")
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = channelName.value,
            onValueChange = { channelName.value = it },
            label = { Text(text = "Channel Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = { onAddChannel(channelName.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(text = "Add")
        }
    }
}