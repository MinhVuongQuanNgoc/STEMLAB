package com.example.stemlab

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntegrationFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun startButton_opensLoginScreen() {
        onView(withId(R.id.btnStart))
            .perform(click())

        onView(withId(R.id.etLoginEmail))
            .check(matches(isDisplayed()))

        onView(withId(R.id.etLoginPassword))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_createAccountButton_opensRegisterScreen() {
        onView(withId(R.id.btnStart))
            .perform(click())

        onView(withId(R.id.btnGoRegister))
            .perform(click())

        onView(withId(R.id.etRegisterEmail))
            .check(matches(isDisplayed()))

        onView(withId(R.id.etRegisterPassword))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_hasCreateAccountButton() {
        onView(withId(R.id.btnStart))
            .perform(click())

        onView(withId(R.id.btnGoRegister))
            .check(matches(isDisplayed()))
    }
}