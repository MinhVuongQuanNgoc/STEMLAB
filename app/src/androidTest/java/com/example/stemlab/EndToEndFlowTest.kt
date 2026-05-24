package com.example.stemlab

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ActivityListActivity::class.java)

    @Test
    fun activityList_toChallengeDetail_toSubmitResult_flowWorks() {
        onView(withText("Parachute Drop Challenge"))
            .perform(click())

        onView(withText("Parachute Drop Challenge"))
            .check(matches(isDisplayed()))

        onView(withText("Engineering + Physics"))
            .check(matches(isDisplayed()))

        onView(withText("Start Challenge"))
            .perform(click())

        onView(withText("Submit Result: Parachute Drop Challenge"))
            .check(matches(isDisplayed()))
    }
}