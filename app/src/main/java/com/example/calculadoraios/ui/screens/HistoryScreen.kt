package com.example.calculadoraios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoraios.R
import com.example.calculadoraios.model.HistoryEntry
import com.example.calculadoraios.ui.components.HistoryItem

private val LIST_PADDING = 16.dp
private val ITEM_SPACING = 8.dp

/**
 * Pantalla de historial: lista de operaciones en orden cronológico
 * o un estado vacío cuando todavía no hay registros.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<HistoryEntry>,
    onBack: () -> Unit
) {
    val background = colorResource(R.color.calculator_background)
    Scaffold(
        containerColor = background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.history_back_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = background,
                    titleContentColor = colorResource(R.color.display_primary_text),
                    navigationIconContentColor = colorResource(R.color.button_operator_background)
                )
            )
        }
    ) { innerPadding ->
        if (history.isEmpty()) {
            EmptyHistory(modifier = Modifier.padding(innerPadding))
        } else {
            HistoryList(history = history, contentPadding = innerPadding)
        }
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.calculator_background)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.history_empty),
            fontSize = 20.sp,
            color = colorResource(R.color.display_secondary_text)
        )
    }
}

@Composable
private fun HistoryList(history: List<HistoryEntry>, contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.calculator_background)),
        contentPadding = PaddingValues(
            start = LIST_PADDING,
            end = LIST_PADDING,
            top = contentPadding.calculateTopPadding() + ITEM_SPACING,
            bottom = contentPadding.calculateBottomPadding() + LIST_PADDING
        ),
        verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)
    ) {
        items(history) { entry ->
            HistoryItem(entry = entry)
        }
    }
}
