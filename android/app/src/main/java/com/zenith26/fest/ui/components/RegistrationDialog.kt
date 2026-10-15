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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald950
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.White
import com.zenith26.fest.ui.theme.ZenithButtonGradient
import kotlinx.coroutines.delay

@Composable
fun RegistrationDialog(
    event: Event,
    isRegistering: Boolean,
    successMessage: String?,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (name: String, roll: String, email: String, phone: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var localNameErr by remember { mutableStateOf<String?>(null) }
    var localRollErr by remember { mutableStateOf<String?>(null) }
    var localEmailErr by remember { mutableStateOf<String?>(null) }

    val isFull = event.availableSeats <= 0

    // Auto-close after successful registration
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(1800)
            onDismiss()
        }
    }

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
                // Top Row: Icon + Title + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate950)
                                .border(1.dp, Slate800, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = event.icon ?: "⚡", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CategoryBadge(category = event.category)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Emerald950)
                                        .border(1.dp, Emerald400.copy(alpha = 0.5f), RoundedCornerShape(50))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (event.fee > 0) "₹${event.fee}" else "Free",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Emerald400
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = White
                            )
                        }
                    }

                    // Close Button ✕
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                Text(
                    text = event.description ?: "No description provided for this event.",
                    fontSize = 12.sp,
                    color = Slate300,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Details Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate950.copy(alpha = 0.6f))
                        .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Date & Time", fontSize = 10.sp, color = Slate400)
                            Text(text = "${event.eventDate} · ${event.eventTime}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate200)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Venue", fontSize = 10.sp, color = Slate400)
                            Text(text = event.venue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate200)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Seats Remaining", fontSize = 10.sp, color = Slate400)
                            Text(
                                text = if (isFull) "0 / ${event.capacity} seats remaining" else "${event.availableSeats} / ${event.capacity} available",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isFull) Rose400 else Purple400
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Status", fontSize = 10.sp, color = Slate400)
                            Text(
                                text = if (isFull) "Registration Closed (FULL)" else "Registration Open",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isFull) Rose400 else Emerald400
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Registration Header
                Text(
                    text = "📝  Register for this Event",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Complete all fields. Your college roll number and email are verified by the server.",
                    fontSize = 11.sp,
                    color = Slate400,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Server Error Feedback Alert
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Rose950.copy(alpha = 0.8f))
                            .border(1.dp, Rose400, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "❌", fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = errorMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Rose400
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Server Success Feedback Alert
                if (successMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Emerald950.copy(alpha = 0.8f))
                            .border(1.dp, Emerald400, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✅", fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = successMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Emerald400
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (!isFull) {
                    // Form Fields
                    InputField(
                        label = "Student Full Name *",
                        value = name,
                        placeholder = "e.g. Alex Johnson",
                        error = localNameErr,
                        onValueChange = {
                            name = it
                            if (it.isNotBlank()) localNameErr = null
                        }
                    )

                    InputField(
                        label = "Roll / Student ID *",
                        value = roll,
                        placeholder = "e.g. 2026CS101",
                        error = localRollErr,
                        onValueChange = {
                            roll = it
                            if (it.isNotBlank()) localRollErr = null
                        }
                    )

                    InputField(
                        label = "College Email Address *",
                        value = email,
                        placeholder = "e.g. alex@university.edu",
                        keyboardType = KeyboardType.Email,
                        error = localEmailErr,
                        onValueChange = {
                            email = it
                            if (it.isNotBlank() && it.contains("@")) localEmailErr = null
                        }
                    )

                    InputField(
                        label = "Phone Number (Optional)",
                        value = phone,
                        placeholder = "e.g. +91 98765 43210",
                        keyboardType = KeyboardType.Phone,
                        error = null,
                        onValueChange = { phone = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Submit Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ZenithButtonGradient)
                            .clickable(enabled = !isRegistering) {
                                var hasError = false
                                if (name.isBlank()) {
                                    localNameErr = "Please enter your full name."
                                    hasError = true
                                }
                                if (roll.isBlank()) {
                                    localRollErr = "Roll number is required."
                                    hasError = true
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    localEmailErr = "Please provide a valid email containing '@'."
                                    hasError = true
                                }
                                if (!hasError) {
                                    onSubmit(name, roll, email, phone)
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRegistering) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Registering...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        } else {
                            Text(
                                text = "Confirm Registration",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                } else {
                    // Full state message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate950)
                            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "This event is at maximum capacity. Registrations are closed.",
                            fontSize = 12.sp,
                            color = Slate400
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputField(
    label: String,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate300,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Slate950)
                .border(1.dp, if (error != null) Rose400 else Slate700, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = Slate700,
                    fontSize = 12.sp
                )
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
        if (error != null) {
            Text(
                text = error,
                fontSize = 10.sp,
                color = Rose400,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}
