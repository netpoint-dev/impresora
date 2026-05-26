@file:Suppress("DEPRECATION")

package com.netpoint.impresora.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Collapsible section with title, icon and colored header */
@Composable
fun CollapsibleSection(
    title: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f))
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = color
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
    }
}

/** Numeric counter with - and + buttons */
@Composable
fun NumericCounter(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = 12,
    step: Int = 1
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        FilledTonalIconButton(
            onClick = { if (value - step >= min) onValueChange(value - step) },
            modifier = Modifier.size(32.dp),
            enabled = value > min
        ) {
            Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
        }
        Text(
            "$value",
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        FilledTonalIconButton(
            onClick = { if (value + step <= max) onValueChange(value + step) },
            modifier = Modifier.size(32.dp),
            enabled = value < max
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
        }
    }
}

/** Three-way toggle for alignment */
@Composable
fun AlignmentSelector(
    selected: Int, // 0=left, 1=center, 2=right
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Alineación:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        listOf(
            Triple(0, Icons.Default.FormatAlignLeft, "Izq"),
            Triple(1, Icons.Default.FormatAlignCenter, "Centro"),
            Triple(2, Icons.Default.FormatAlignRight, "Der")
        ).forEach { (mode, icon, label) ->
            FilterChip(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                label = { Text(label, fontSize = 11.sp) },
                leadingIcon = { Icon(icon, null, modifier = Modifier.size(14.dp)) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

/** Status indicator LED */
@Composable
fun StatusLed(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

/** Small action button that fits the compact layout */
@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, fontSize = 12.sp)
    }
}

/** Labeled toggle switch */
@Composable
fun LabeledSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.height(24.dp)
        )
    }
}

/** Gray level visual bar indicator */
@Composable
fun GrayLevelBar(value: Int, max: Int) {
    val fraction = if (max > 0) value.toFloat() / max else 0f
    val barColor = when {
        fraction > 0.75f -> Color(0xFFE53935) // red warning
        fraction > 0.5f -> Color(0xFFFFA726) // orange
        else -> Color(0xFF66BB6A) // green
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
        if (fraction > 0.75f) {
            Text(
                "⚠ Nivel alto: riesgo de sobrecalentamiento",
                color = Color(0xFFE53935),
                fontSize = 10.sp
            )
        }
    }
}

/** Speed level visual bar indicator */
@Composable
fun SpeedLevelBar(value: Int, max: Int) {
    val fraction = if (max > 0) value.toFloat() / max else 0f
    val barColor = when {
        fraction > 0.75f -> Color(0xFFE53935)
        fraction > 0.4f  -> Color(0xFFFFA726)
        else             -> Color(0xFF66BB6A)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mín (0)", "Med (1)", "Máx (2)").forEach { label ->
                Text(label, fontSize = 9.sp, color = Color.Gray)
            }
        }
    }
}
