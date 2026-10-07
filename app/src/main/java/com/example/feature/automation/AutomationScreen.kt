package com.example.feature.automation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun AutomationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reminders by viewModel.reminders.collectAsState()
    val automations by viewModel.automations.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    var activeTab by remember { mutableStateOf("REMINDERS") } // REMINDERS, AUTOMATIONS, VIP_CONTACTS
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AUTOMATION & VIP ⚡",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Smart routines, scheduled reminders and VIP contacts",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            IconButton(
                onClick = {
                    if (activeTab == "REMINDERS") showAddReminderDialog = true
                    else if (activeTab == "VIP_CONTACTS") showAddContactDialog = true
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = NeonCyan, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "REMINDERS" to "⏰ Reminders",
                "AUTOMATIONS" to "⚡ Routines",
                "VIP_CONTACTS" to "👑 Prime VIP"
            ).forEach { (key, label) ->
                val isSelected = activeTab == key
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else GlassSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else GlassBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonCyan else Color.White,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (activeTab) {
                "REMINDERS" -> {
                    if (reminders.isEmpty()) {
                        item {
                            Text("No reminders scheduled. Tap '+' to create one.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    items(reminders) { item ->
                        LiquidGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = NeonPink
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Scheduled: ${item.timeDisplay}", fontSize = 11.sp, color = NeonCyan)
                                }
                                IconButton(onClick = { viewModel.deleteReminder(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                "AUTOMATIONS" -> {
                    items(automations) { rule ->
                        LiquidGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = ElectricViolet
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = rule.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GlowGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(text = "ENABLED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GlowGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Trigger: ${rule.triggerType} • Action: ${rule.actionType}", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                            }
                        }
                    }
                }

                "VIP_CONTACTS" -> {
                    items(contacts) { contact ->
                        LiquidGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = WarmAmber
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "${contact.name} (${contact.nickname})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(8.dp), color = WarmAmber.copy(alpha = 0.2f)) {
                                            Text(text = contact.relationship, fontSize = 9.sp, color = WarmAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Greeting: \"${contact.customGreeting}\"", fontSize = 12.sp, color = NeonPink)
                                    Text(text = "Phone: ${contact.phoneNumber}", fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.deleteContact(contact.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Reminder Dialog
    if (showAddReminderDialog) {
        var rTitle by remember { mutableStateOf("") }
        var rTime by remember { mutableStateOf("Aaj shaam 7:00 PM") }
        AlertDialog(
            onDismissRequest = { showAddReminderDialog = false },
            title = { Text("Schedule Reminder ⏰", color = NeonCyan) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = rTitle, onValueChange = { rTitle = it }, label = { Text("Task Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rTime, onValueChange = { rTime = it }, label = { Text("Time or Duration") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rTitle.isNotBlank()) {
                            viewModel.addReminder(rTitle, rTime)
                            showAddReminderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Schedule", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddReminderDialog = false }) { Text("Cancel", color = Color.White) }
            },
            containerColor = SpaceCardBg
        )
    }

    // Add Contact Dialog
    if (showAddContactDialog) {
        var cName by remember { mutableStateOf("") }
        var cPhone by remember { mutableStateOf("+91 ") }
        var cRelation by remember { mutableStateOf("Friend") }
        var cNick by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Add Prime VIP Contact 👑", color = WarmAmber) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = cName, onValueChange = { cName = it }, label = { Text("Contact Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cNick, onValueChange = { cNick = it }, label = { Text("Nickname (e.g. Jaan)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cPhone, onValueChange = { cPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cRelation, onValueChange = { cRelation = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cName.isNotBlank() && cPhone.isNotBlank()) {
                            viewModel.addPrimeContact(cName, cPhone, cRelation, cNick.ifBlank { cName })
                            showAddContactDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber)
                ) {
                    Text("Save VIP", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddContactDialog = false }) { Text("Cancel", color = Color.White) }
            },
            containerColor = SpaceCardBg
        )
    }
}
