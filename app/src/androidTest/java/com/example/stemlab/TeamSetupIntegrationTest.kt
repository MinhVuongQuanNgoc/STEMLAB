package com.example.stemlab

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
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
class TeamSetupIntegrationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TeamSetupActivity::class.java)

    @Test
    fun createTeam_opensActivityListScreen() {
        onView(withId(R.id.etTeamName))
            .perform(typeText("Test Team"), closeSoftKeyboard())

        onView(withId(R.id.etMemberOne))
            .perform(typeText("Alex"), closeSoftKeyboard())

        onView(withId(R.id.etMemberTwo))
            .perform(typeText("Ben"), closeSoftKeyboard())

        onView(withId(R.id.etGradeLevel))
            .perform(typeText("Year 7"), closeSoftKeyboard())

        onView(withId(R.id.btnCreateTeam))
            .perform(click())

        onView(withText("STEMM Activities"))
            .check(matches(isDisplayed()))
    }
}