package com.example.schef.gadgeothek;

import android.app.Activity;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.schef.domain.Constants;
import com.example.schef.service.DBService;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GadgetlistInstrumentedTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void clearAll() throws InterruptedException {
        Activity activity = mActivityRule.getActivity();
        activity.getSharedPreferences(Constants.SHARED_PREF, Activity.MODE_PRIVATE).edit().clear().apply();
        DBService.getDBService(activity);
        DBService.clearDB();
        Thread.sleep(1000);

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.serverName)).perform(typeText("Testserver 1"), closeSoftKeyboard());
        onView(withId(R.id.serverUri)).perform(typeText("mge1.dev.ifs.hsr.ch"), closeSoftKeyboard());
        onView(withText(R.string.server_add_button)).perform(click());

        Thread.sleep(1000);

        onView(withText("Testserver 1")).perform(click());
        onView(withId(R.id.emailEditText)).perform(typeText("miep@miep.miep"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("miep"), closeSoftKeyboard());
        onView(withText(R.string.login_button)).perform(click());
    }

    @After
    public void clearAfter() {
        Activity activity = mActivityRule.getActivity();
        activity.getSharedPreferences(Constants.SHARED_PREF, Activity.MODE_PRIVATE).edit().clear().apply();
        DBService.getDBService(activity);
        DBService.clearDB();
    }

    @Test
    public void testListHasElements() {
        onView(withId(R.id.gadgetlistRecyclerView)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                RecyclerView gadgetlist = (RecyclerView) view;
                int count = gadgetlist.getAdapter().getItemCount();
                org.junit.Assert.assertTrue("List has elements", count > 0);
            }
        });
    }

    @Test
    public void testReserveGadget() throws InterruptedException {
        onView(withId(R.id.gadgetlistRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.reserveGadgetButton)).perform(click());

        Thread.sleep(1000);

        onView(withText(R.string.reserve_gadget_no_success)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));
    }
}
