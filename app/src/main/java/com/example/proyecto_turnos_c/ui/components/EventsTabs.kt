package com.example.proyecto_turnos_c.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EventTabs(
    content: @Composable () -> Unit,
    finishedContent: @Composable () -> Unit,
    containerColor: Color = Color.Transparent
) {
    val selectedTabIndex = remember { mutableStateOf(0) }

    Column {
        // Fila de pestañas sin indicador activo (usamos una lambda vacía en "indicator")
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex.value,
            containerColor = containerColor,
            indicator = {} // Sin indicador activo
        ) {
            Tab(
                selected = selectedTabIndex.value == 0,
                onClick = { selectedTabIndex.value = 0 }
            ) {
                Text(
                    text = "Vigentes",
                    color = if (selectedTabIndex.value == 0) Color(0xFFFFA500) else Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
            Tab(
                selected = selectedTabIndex.value == 1,
                onClick = { selectedTabIndex.value = 1 }
            ) {
                Text(
                    text = "Historial",
                    color = if (selectedTabIndex.value == 1) Color(0xFFFFA500) else Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
        // Renderiza el contenido según la pestaña seleccionada.
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex.value) {
                0 -> content()
                1 -> finishedContent()
            }
        }
    }
}


@Preview
@Composable
fun EventTabsPreview() {
    EventTabs(
        content = {
            Text(
                text = "asa",
                color = Color.Green,
                )
        },
        finishedContent = {
            Text(
                text = "vencidos",
                color = Color.Green,
            )
        }
    )

}