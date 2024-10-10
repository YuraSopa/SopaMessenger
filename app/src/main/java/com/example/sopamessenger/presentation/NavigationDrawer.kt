package com.example.sopamessenger.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sopamessenger.DrawerMenuItem

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(200.dp)
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Header", fontSize = 60.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
}

val itemsDrawerMenu = listOf(
    DrawerMenuItem(
        id = "home",
        title = "Home",
        contentDescription = "Go to Home Screen",
        icon = Icons.Default.Home
    ),
    DrawerMenuItem(
        id = "settings",
        title = "Settings",
        contentDescription = "Go to Settings",
        icon = Icons.Default.Settings
    ),
    DrawerMenuItem(
        id = "logout",
        title = "Log out",
        contentDescription = "Log out your account",
        icon = Icons.AutoMirrored.Default.ExitToApp
    )
)