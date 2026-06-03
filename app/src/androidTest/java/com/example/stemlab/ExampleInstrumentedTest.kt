package com.example.stemlab

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainScreen_displaysAppTitle() {
        onView(withId(R.id.tvTitle))
            .check(matches(withText(R.string.title_stem_lab)))
    }

    @Test
    fun mainScreen_displaysSubtitle() {
        onView(withId(R.id.tvSubtitle))
            .check(matches(withText(R.string.subtitle_main)))
    }

    @Test
    fun mainScreen_displaysStartButton() {
        onView(withId(R.id.btnStart))
            .check(matches(withText(R.string.btn_start)))
    }
}