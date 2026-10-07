package com.example.feature.memory

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun MemoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsState()
    val isOwnerUnlocked by viewModel.ownerAuthManager.isOwnerUnlocked.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("PUBLIC") } // PUBLIC or PRIVATE
    var searchQuery by remember { mutableStateOf("") }

    val filteredMemories = remember(memories, selectedTab, searchQuery, isOwnerUnlocked) {
        memories.filter {
            if (selectedTab == "PRIVATE") it.isPrivate else !it.isPrivate
        }.filter {
            if (searchQuery.isBlank()) true
            else it.title.contains(searchQuery, true) || it.content.contains(searchQuery, true)
        }
    }

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
                    text = "MEMORY VAULT 🧠",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Persistent long-term memories remembered by MYRA",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NeonCyan,
                contentColor = Color.Black,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("add_memory_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add memory")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search memories...", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = GlassBorder
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Public vs Private Vault Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (selectedTab == "PUBLIC") NeonCyan.copy(alpha = 0.2f) else GlassSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedTab == "PUBLIC") NeonCyan else GlassBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = "PUBLIC" }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bookmarks, contentDescription = "Public", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "General Memories", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (selectedTab == "PRIVATE") NeonPink.copy(alpha = 0.2f) else GlassSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedTab == "PRIVATE") NeonPink else GlassBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (!isOwnerUnlocked) {
                            showPinDialog = true
                        } else {
                            selectedTab = "PRIVATE"
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isOwnerUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = "Private",
                        tint = NeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Private Vault 🔒", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Memories List
        if (selectedTab == "PRIVATE" && !isOwnerUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = NeonPink, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Private Memories are Protected", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Enter Owner PIN to unlock private vault", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showPinDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                    ) {
                        Text("Unlock Private Vault", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredMemories.isEmpty()) {
                    item {
                        Text(
                            text = "No memories stored in this vault yet.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
                items(filteredMemories) { memory ->
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (memory.isPrivate) NeonPink else NeonCyan,
                        testTag = "memory_item_${memory.id}"
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = memory.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GlassSurface
                                    ) {
                                        Text(
                                            text = memory.category,
                                            fontSize = 9.sp,
                                            color = NeonCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = memory.content,
                                    fontSize = 13.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 18.sp
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteMemory(memory.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete memory",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Memory Dialog
    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newContent by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Preferences") }
        var isPrivate by remember { mutableStateOf(selectedTab == "PRIVATE") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Store New Memory", color = NeonCyan, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Topic / Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Memory Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isPrivate, onCheckedChange = { isPrivate = it })
                        Text("Store in Encrypted Private Vault", fontSize = 12.sp, color = Color.White)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            viewModel.addMemory(newTitle, newContent, newCategory, isPrivate)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Remember", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = SpaceCardBg
        )
    }

    // PIN Verification Dialog
    if (showPinDialog) {
        var inputPin by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Owner Verification 🛡️", color = NeonPink, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter Owner PIN (Default: 1234)", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputPin,
                        onValueChange = { inputPin = it; pinError = false },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError) {
                        Text("Incorrect PIN. Access Denied.", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = viewModel.ownerAuthManager.verifyPin(inputPin)
                        if (ok) {
                            showPinDialog = false
                            selectedTab = "PRIVATE"
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("Unlock", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPinDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = SpaceCardBg
        )
    }
}
