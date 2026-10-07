package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.auth.SignInScreen
import com.example.feature.automation.AutomationScreen
import com.example.feature.chat.ConversationHistoryScreen
import com.example.feature.emotion.EmotionStudioScreen
import com.example.feature.home.HomeScreen
import com.example.feature.memory.MemoryScreen
import com.example.feature.personality.PersonalityStudioScreen
import com.example.feature.security.SecurityCenterScreen
import com.example.feature.voice.VoiceStudioScreen
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

enum class MyraScreen(val title: String, val icon: ImageVector) {
    HOME("MYRA", Icons.Default.Adjust),
    VOICE("Voice", Icons.Default.GraphicEq),
    EMOTION("Emotion", Icons.Default.Favorite),
    PERSONALITY("Style", Icons.Default.AutoAwesome),
    MEMORY("Memory", Icons.Default.Psychology),
    AUTOMATION("Auto", Icons.Default.Bolt),
    SECURITY("Security", Icons.Default.Security),
    CHAT("Chat", Icons.Default.ChatBubbleOutline)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.authManager.currentUser.collectAsState()

                if (currentUser == null) {
                    SignInScreen(authManager = viewModel.authManager)
                } else {
                    MainAppScaffold(viewModel = viewModel, userEmail = currentUser?.email ?: "Rahul")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    viewModel: MainViewModel,
    userEmail: String
) {
    var currentScreen by remember { mutableStateOf(MyraScreen.HOME) }

    // Android back button handling
    BackHandler(enabled = currentScreen != MyraScreen.HOME) {
        currentScreen = MyraScreen.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SpaceDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MYRA",
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            fontSize = 20.sp,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GlassSurface
                        ) {
                            Text(
                                text = "AI COMPANION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Current Signed-in User Chip
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GlassSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier
                            .clickable { viewModel.authManager.signOut() }
                            .padding(end = 12.dp)
                            .testTag("user_profile_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GlowGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = userEmail.substringBefore("@"),
                                fontSize = 11.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sign out",
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceDark.copy(alpha = 0.95f),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Horizontal scrollable futuristic glass navigation bar
            Surface(
                color = SpaceCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MyraScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.22f) else Color.Transparent,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                            modifier = Modifier
                                .clickable { currentScreen = screen }
                                .testTag("nav_tab_${screen.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) NeonCyan else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                MyraScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToVoiceStudio = { currentScreen = MyraScreen.VOICE },
                    onNavigateToEmotionStudio = { currentScreen = MyraScreen.EMOTION }
                )
                MyraScreen.VOICE -> VoiceStudioScreen(viewModel = viewModel)
                MyraScreen.EMOTION -> EmotionStudioScreen(viewModel = viewModel)
                MyraScreen.PERSONALITY -> PersonalityStudioScreen(viewModel = viewModel)
                MyraScreen.MEMORY -> MemoryScreen(viewModel = viewModel)
                MyraScreen.AUTOMATION -> AutomationScreen(viewModel = viewModel)
                MyraScreen.SECURITY -> SecurityCenterScreen(viewModel = viewModel)
                MyraScreen.CHAT -> ConversationHistoryScreen(viewModel = viewModel)
            }
        }
    }
}
