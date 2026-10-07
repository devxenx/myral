package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.SpaceCardBg

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    testTag: String = "liquid_glass_card",
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        color = SpaceCardBg,
        border = BorderStroke(
            borderWidth,
            Brush.linearGradient(
                listOf(
                    borderColor.copy(alpha = 0.6f),
                    borderColor.copy(alpha = 0.15f),
                    borderColor.copy(alpha = 0.4f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x25FFFFFF),
                            Color(0x05FFFFFF),
                            Color(0x10000000)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}
