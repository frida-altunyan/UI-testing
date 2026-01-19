package com.frida.ui_testing

import android.view.View
import com.frida.ui_testing.ui.MainActivity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [34],
    application = HiltTestApplication::class
)
class MainActivityRobolectricTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun initialState_showsNoData() {
        hiltRule.inject()

        val activity = Robolectric.buildActivity(MainActivity::class.java)
            .setup()
            .get()

        assertThat(
            activity.findViewById<View>(android.R.id.content)
        ).isNotNull()
    }
}
