package com.example.stemlab

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
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
        onView(withId(R.id.btnParachute))
            .perform(click())

        onView(withId(R.id.tvChallengeTitle))
            .check(matches(withText(R.string.btn_parachute)))

        onView(withId(R.id.tvChallengeCategory))
            .check(matches(withText("Engineering + Physics")))

        onView(withId(R.id.btnStartChallenge))
            .perform(click())

        onView(withId(R.id.tvSubmitTitle))
            .check(matches(withText("Submit Result: Parachute Drop Challenge")))
    }
}