package com.example.calculadoraios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoraios.R
import com.example.calculadoraios.model.HistoryEntry

/** Fila del historial: expresión en gris y resultado destacado, alineados a la derecha. */
@Composable
fun HistoryItem(entry: HistoryEntry, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(R.color.history_item_background))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "${entry.firstOperand} ${operationSymbol(entry.operation)} ${entry.secondOperand}",
            fontSize = 20.sp,
            color = colorResource(R.color.display_secondary_text),
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${stringResource(R.string.symbol_equals)} ${entry.result}",
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.display_primary_text),
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
