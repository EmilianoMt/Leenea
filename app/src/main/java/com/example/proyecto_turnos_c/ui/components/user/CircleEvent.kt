package com.example.proyecto_turnos_c.ui.components.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.proyecto_turnos_c.R

@Composable
fun CircularImageCard(
    imageUrl: String? = null,
    imageRes: Int? = null,
    title: String,
    description: String,
    isAvailable: Boolean,
    action: () -> Unit
) {
    // Determine the appropriate painter based on the image source
    val painter = when {
        imageUrl != null && imageUrl.startsWith("http") -> {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build()
            )
        }
        imageRes != null -> {
            painterResource(id = imageRes)
        }
        else -> {
            painterResource(id = R.drawable.event1) // Default fallback image
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(CardDefaults.elevatedShape)
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable{action()},
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Event Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (!isAvailable) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Gray.copy(alpha = 0.6f))
            )
        }
    }
}

//@Preview
//@Composable
//fun CircularImageCardPreview() {
//    CircularImageCard(
//        imageRes = R.drawable.event1,
//        title = "Evento 1",
//        description = "Descripción del evento 1",
//        isAvailable = true
//    )
//}
//
//@Preview
//@Composable
//fun CircularImageCardPreview2() {
//    CircularImageCard(
//        imageRes = R.drawable.event1,
//        title = "Evento 1",
//        description = "Descripción del evento 1",
//        isAvailable = false
//    )
//}
//
//@Preview
//@Composable
//fun CircularImageCardUrlPreview() {
//    CircularImageCard(
//        imageUrl = "https://example.com/image.jpg",
//        title = "Evento con URL",
//        description = "Descripción del evento con URL",
//        isAvailable = true
//    )
//}