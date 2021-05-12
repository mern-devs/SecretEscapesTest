package com.secretescapes.test

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test

class FeedFragmentTest {

    /**
     * Scrolls to position 10 and asserts that "Sale in position: 10" is displayed.
     */
    @Test
    fun saleTitle_InPosition10_shouldBeCorrect() {
        launchFragmentInContainer(themeResId = R.style.Theme_SecretEscapesTest) { FeedFragment(true) }

        /********* Don't change below this line please **********/
        onView(withId(R.id.recycler_view)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(10)
        )
        onView(
            withText(containsString("A Sale in position: 10"))
        ).check(matches(isDisplayed()))
    }
}
