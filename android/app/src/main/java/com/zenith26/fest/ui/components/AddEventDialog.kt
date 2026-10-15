package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.White

@Composable
fun AddEventDialog(
    isCreating: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (EventCreateIn) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technical") }
    var icon by remember { mutableStateOf("🎪") }
    var date by remember { mutableStateOf("2026-11-14") }
    var time by remember { mutableStateOf("10:00 AM") }
    var venue by remember { mutableStateOf("") }
    var capacityStr by remember { mutableStateOf("50") }
    var feeStr by remember { mutableStateOf("0") }
    var description by remember { mutableStateOf("") }

    var localErr by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(Slate900)
                .border(1.dp, Slate700, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "➕  Create New Fest Event",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "✕",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (errorMessage != null || localErr != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Rose950)
                            .border(1.dp, Rose400, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = errorMessage ?: localErr ?: "",
                            fontSize = 11.sp,
                            color = Rose400
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                SimpleInputField("Event Title *", title, "e.g. Drone Obstacle Race") { title = it }
                SimpleInputField("Category *", category, "Technical, Cultural, Sports, etc.") { category = it }
                SimpleInputField("Icon (Emoji)", icon, "🎪") { icon = it }
                SimpleInputField("Date (YYYY-MM-DD) *", date, "2026-11-14") { date = it }
                SimpleInputField("Time *", time, "10:00 AM") { time = it }
                SimpleInputField("Venue *", venue, "Campus Ground") { venue = it }
                SimpleInputField("Capacity *", capacityStr, "50", KeyboardType.Number) { capacityStr = it }
                SimpleInputField("Fee (INR, 0 for Free)", feeStr, "0", KeyboardType.Number) { feeStr = it }
                SimpleInputField("Description", description, "Event overview...") { description = it }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Purple600)
                        .clickable(enabled = !isCreating) {
                            val cap = capacityStr.toIntOrNull() ?: 0
                            val fee = feeStr.toIntOrNull() ?: 0
                            if (title.isBlank()) {
                                localErr = "Event title is required."
                            } else if (venue.isBlank()) {
                                localErr = "Venue is required."
                            } else if (cap <= 0) {
                                localErr = "Capacity must be greater than 0."
                            } else {
                                localErr = null
                                onSubmit(
                                    EventCreateIn(
                                        title = title.trim(),
                                        category = category.trim(),
                                        description = description.trim().ifBlank { null },
                                        eventDate = date.trim(),
                                        eventTime = time.trim(),
                                        venue = venue.trim(),
                                        capacity = cap,
                                        fee = fee,
                                        icon = icon.trim().ifBlank { "🎪" }
                                    )
                                )
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCreating) "Saving Event..." else "Save Event to Database",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleInputField(
    label: String,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate300)
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Slate950)
                .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            if (value.isEmpty()) {
                Text(text = placeholder, color = Slate700, fontSize = 12.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = White, fontSize = 12.sp),
                cursorBrush = SolidColor(Purple500),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
