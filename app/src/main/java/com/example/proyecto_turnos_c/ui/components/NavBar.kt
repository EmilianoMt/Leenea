import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun NavBar(modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Events", "Profile")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Event, Icons.Filled.AccountCircle)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Event, Icons.Outlined.AccountCircle)

    NavigationBar(
        containerColor = Color(0xFF191C88),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .then(modifier),

    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        tint = if (selectedItem == index) Color(0xFFFFA73B) else Color(0xFFB0BEC5),
                        imageVector = if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item
                    )
                },
                label = { Text(text = item ,
                    color = if (selectedItem == index) Color(0xFFFFA73B) else MaterialTheme.colorScheme.onPrimary ) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
