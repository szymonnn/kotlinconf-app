package org.jetbrains.kotlinconf.ui

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward
import org.hamcrest.CoreMatchers.allOf
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.KotlinConf
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.storage.ApplicationContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class WelcomeActivityUiTest {

    @get:Rule
    val activityRule = ActivityTestRule(WelcomeActivity::class.java)


    @Before
    fun init() {
        KotlinConf.service = ConferenceService(ApplicationContext(activityRule.activity, 0))
    }

    @Test
    fun privacyPolicySmokeTest() {
        assertDisplayed("PRIVACY POLICY")
        assertDisplayed("Reject")
        assertDisplayed("Accept")
    }

    @Test
    fun privacyPolicyContentClickTest() {
        Intents.init()
        val expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData("https://www.jetbrains.com/company/privacy.html"))
        intending(expectedIntent).respondWith(ActivityResult(0, null))
        clickOn(R.id.privacy_policy_text)
        intended(expectedIntent)
        Intents.release()
    }

    @Test
    fun privacyPolicyAcceptClickTest() {
        clickOn(R.id.welcome_privacy_accept)
        assertDisplayed("DO YOU WANT REMINDERS?")
    }

    @Test
    fun privacyPolicyRejectClickTest() {
        clickOn(R.id.welcome_privacy_next)
        assertDisplayed("DO YOU WANT REMINDERS?")
    }

    @Test
    fun notificationsSmokeTest() {
        swipeViewPagerForward(R.id.welcome_pager)
        assertDisplayed("DO YOU WANT REMINDERS?")
        assertDisplayed("Reject")
        assertDisplayed("Allow")
    }
}