package com.etb.app;

import android.content.ComponentName;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.etb.app.activity.HotelListActivity;
import com.etb.app.activity.MainActivity;
import com.etb.app.events.Events;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * @author alex
 * @date 2015-06-21
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    private MainActivity mActivity;

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();

    }

    @After
    public void tearDown() {
    }

    @Test
    public void changeText_sameActivity() {
//        bookingFlowTest();

//        // Check that the text was changed.
//        onView(withId(R.id.app_bar)).check(matches(withText("Amsterdam, Netherlands")));
    }

    @Test
    public void changeText_newActivity() {
        bookingFlowTest();
    }

    private void bookingFlowTest() {
        // Type text and then press the button.
        autocomplete("Amsterdam", "Amsterdam, Netherlands");

        selectDates();

        selectPersons("4");

        selectRooms("3");

        selectRooms("1");

        selectPersons("2");

        onView(withId(R.id.search_hotels)).perform(click());

    }

    private void selectRooms(String rooms) {
        onView(withId(R.id.rooms_button))
                .perform(click());

        onView(withId(R.id.number_rooms_selector))
                .check(matches(isDisplayed()));

        onView(allOf(
                withText(rooms),
                withParent(withId(R.id.number_rooms_selector))
        )).perform(click());

        onView(withId(R.id.number_rooms_selector))
                .check(matches(not(isDisplayed())));

        onView(allOf(
                withId(R.id.number),
                withParent(withId(R.id.rooms_button))
        )).check(matches(withText(rooms)));

    }

    private void selectPersons(String persons) {
        onView(withId(R.id.persons_button))
                .perform(click());

        onView(withId(R.id.number_persons_selector))
                .check(matches(isDisplayed()));

        onView(allOf(
                withText(persons),
                withParent(withId(R.id.number_persons_selector))
        )).perform(click());

        onView(withId(R.id.number_persons_selector))
                .check(matches(not(isDisplayed())));

        onView(allOf(
                withId(R.id.number),
                withParent(withId(R.id.persons_button))
        )).check(matches(withText(persons)));

    }

    private void selectDates() {
        onView(withId(R.id.check_in_button))
                .perform(click());

        onView(withId(R.id.datepicker_arrow_right))
                .perform(click());

        onView(withId(R.id.datepicker_day))
                .perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.VISIBLE_CENTER, Press.FINGER));

    }

    private void autocomplete(String stringToBeTyped, String textToSelect) {
        onView(withId(R.id.autoCompleteTextView_location))
                .perform(clearText())
                .perform(typeText(stringToBeTyped), closeSoftKeyboard());

        onView(withText(textToSelect))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withText(textToSelect))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.autoCompleteTextView_location)).check(matches(withText(textToSelect)));


    }
}
