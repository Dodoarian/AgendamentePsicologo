package com.example.agendamente.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopBarNavigation(
    navigateBack: () -> Unit,
    child: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.padding(10.dp)
            ) {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {
            child()
        }

    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 620)
@Composable
private fun TopBarNavigationPreview() {
    TopBarNavigation({}) {
        Text("Teste")
    }
}