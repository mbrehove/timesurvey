package com.timesurvey.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.timesurvey.data.database.Category

@Composable
fun CategoryList(categories: List<Category>) {
    ScalingLazyColumn(modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            Chip(
                onClick = { /* TODO: Handle category selection */ },
                label = { Text(text = category.name) }
            )
        }
    }
}