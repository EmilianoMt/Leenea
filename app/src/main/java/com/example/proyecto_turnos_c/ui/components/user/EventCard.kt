package com.example.proyecto_turnos_c.ui.components.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.proyecto_turnos_c.R

@Composable
fun EventCard(
    title: String,
    subtitle: String,
    image: String,
    action: () -> Unit
) {
    val painter = if (image.startsWith("http")) {
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(image)
                .crossfade(true)
                .build()
        )
    } else {
        androidx.compose.ui.res.painterResource(id = R.drawable.event1)
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .size(width = 370.dp, height = 210.dp)
            .clickable { action() }
    ) {
        Column {
            Image(
                painter = painter,
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Siguiente",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(bottom = 8.dp)
                            .align(Alignment.BottomEnd)
                    )
            }
        }
    }
}