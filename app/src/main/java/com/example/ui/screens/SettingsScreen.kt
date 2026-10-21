package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldSuccess
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurplePrimary
import com.example.ui.viewmodel.MilestoneViewModel

@Composable
fun SettingsScreen(
    viewModel: MilestoneViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsRepo = viewModel.settingsRepository

    val currentReminder by settingsRepo.reminderFrequency.collectAsState()
    val currentTheme by settingsRepo.themePreference.collectAsState()
    val showQuotes by settingsRepo.showMotivationalQuotes.collectAsState()
    val celebrationEffects by settingsRepo.celebrationEffects.collectAsState()

    var showReminderDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                    Text(
                        text = "Settings & Preferences",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customize your Milestone experience",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Section 1: Reminders & Notifications
            item {
                SettingsGroupCard(title = "Reminders & Alerts") {
                    SettingsActionRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Reminder Frequency",
                        subtitle = currentReminder,
                        onClick = { showReminderDialog = true },
                        modifier = Modifier.testTag("reminder_frequency_setting")
                    )
                }
            }

            // Section 2: Appearance & Theme
            item {
                SettingsGroupCard(title = "Appearance & Styling") {
                    SettingsActionRow(
                        icon = Icons.Default.DarkMode,
                        title = "Theme",
                        subtitle = currentTheme,
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.testTag("theme_setting")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsToggleRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Motivational Quotes",
                        subtitle = "Inspiring quotes and daily momentum banner on home",
                        checked = showQuotes,
                        onCheckedChange = { settingsRepo.setShowMotivationalQuotes(it) },
                        modifier = Modifier.testTag("quotes_toggle")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsToggleRow(
                        icon = Icons.Default.Vibration,
                        title = "Celebration Effects",
                        subtitle = "Confetti bursts when milestones & goals are reached",
                        checked = celebrationEffects,
                        onCheckedChange = { settingsRepo.setCelebrationEffects(it) },
                        modifier = Modifier.testTag("celebration_toggle")
                    )
                }
            }

            // Section 3: Data Management
            item {
                SettingsGroupCard(title = "Data Management") {
                    SettingsActionRow(
                        icon = Icons.Default.RestartAlt,
                        title = "Reset to Sample Goals",
                        subtitle = "Load inspiring default goals & milestones",
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier.testTag("reset_sample_data_button")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsActionRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Clear All Goals & Milestones",
                        subtitle = "Delete all stored data and start fresh",
                        onClick = { showClearConfirmDialog = true },
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("clear_all_data_button")
                    )
                }
            }

            // Section 4: About Milestone
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏔️", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Milestone v1.0",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Built to inspire consistent progress, celebrate victories, and help you reach every summit.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Reminder Frequency Dialog
    if (showReminderDialog) {
        val frequencies = listOf(
            "Daily Morning (8:30 AM)",
            "Midday Check-in (1:00 PM)",
            "Evening Reflection (8:00 PM)",
            "Weekly Summary (Monday)",
            "Off"
        )
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Select Reminder Frequency", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    frequencies.forEach { freq ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsRepo.setReminderFrequency(freq)
                                    showReminderDialog = false
                                    Toast.makeText(context, "Reminder set to $freq", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentReminder == freq,
                                onClick = {
                                    settingsRepo.setReminderFrequency(freq)
                                    showReminderDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = freq, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showReminderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Theme Selector Dialog
    if (showThemeDialog) {
        val themes = listOf(
            "Light (#FAFAFA)",
            "Dark (#0F0D17)",
            "System Default"
        )
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    themes.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsRepo.setThemePreference(theme)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == theme,
                                onClick = {
                                    settingsRepo.setThemePreference(theme)
                                    showThemeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = theme, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset to Sample Goals?") },
            text = { Text("This will reload the inspiring starter goals for Marathon, Mobile App MVP, Jetpack Compose, Emergency Fund, and Rainier summit.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToSampleData()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "Sample goals loaded!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Load Sample Goals")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Data?") },
            text = { Text("This will permanently delete all goals and milestones. You can restore sample goals at any time from this settings screen.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearConfirmDialog = false
                        Toast.makeText(context, "All goals cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (textColor == MaterialTheme.colorScheme.error) textColor else PurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PurplePrimary
            )
        )
    }
}
