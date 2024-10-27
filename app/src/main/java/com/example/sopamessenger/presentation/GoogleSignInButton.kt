package com.example.sopamessenger.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sopamessenger.R

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit
) {
     IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            modifier = Modifier.size(50.dp),
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Icon", tint = Color.Unspecified
        )
    }

}