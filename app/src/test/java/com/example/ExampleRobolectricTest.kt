package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.command.AdvancedCommandParser
import com.example.core.command.ParsedCommand
import com.example.core.emotion.EmotionEngine
import com.example.core.emotion.MyraEmotion
import com.example.core.owner.OwnerAuthenticationManager
import com.example.core.personality.RelationshipMode
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MYRA", appName)
    }

    @Test
    fun `test emotion detection from Hinglish and Hindi text`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val emotionEngine = EmotionEngine(context)

        val comforting = emotionEngine.detectEmotionFromText("Aaj mera din bohot bura tha aur main udas hoon")
        assertEquals(MyraEmotion.COMFORTING, comforting)

        val loving = emotionEngine.detectEmotionFromText("MYRA I love you tum meri jaan ho")
        assertEquals(MyraEmotion.LOVING, loving)

        val cute = emotionEngine.detectEmotionFromText("Tum kitni cute aur sweet ho")
        assertEquals(MyraEmotion.CUTE, cute)

        val happy = emotionEngine.detectEmotionFromText("Aaj mera exam result bohot accha aaya main khush hoon")
        assertEquals(MyraEmotion.HAPPY, happy)
    }

    @Test
    fun `test command parser mode changes and sensitive actions`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parser = AdvancedCommandParser(context)

        val loveCmd = parser.parse("MYRA love mode on karo")
        assertTrue(loveCmd is ParsedCommand.ModeChange)
        assertEquals(RelationshipMode.LOVE, (loveCmd as ParsedCommand.ModeChange).mode)

        val cuteCmd = parser.parse("MYRA cute mode")
        assertTrue(cuteCmd is ParsedCommand.ModeChange)
        assertEquals(RelationshipMode.CUTE, (cuteCmd as ParsedCommand.ModeChange).mode)

        val torchCmd = parser.parse("torch on")
        assertTrue(torchCmd is ParsedCommand.Flashlight)
        assertTrue((torchCmd as ParsedCommand.Flashlight).enable)

        val volumeCmd = parser.parse("volume 40 percent kar do")
        assertTrue(volumeCmd is ParsedCommand.Volume)
        assertEquals(40, (volumeCmd as ParsedCommand.Volume).percent)

        // Financial Confirmation security invariant
        val paymentCmd = parser.parse("Rahul ko ₹500 payment kar do")
        assertTrue(paymentCmd is ParsedCommand.RequireConfirmation)
    }

    @Test
    fun `test owner authentication PIN verification`() {
        val authManager = OwnerAuthenticationManager()
        // Default PIN is 1234
        assertTrue(authManager.verifyPin("1234"))
        assertFalse(authManager.verifyPin("9999"))
    }
}
