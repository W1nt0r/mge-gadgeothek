package com.example.schef.gadgeothek;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.view.View;

import com.example.schef.domain.Constants;
import com.example.schef.service.DBService;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScenarioInstrumentedTest extends InstrumentationTestCase {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void clearAllBefore() throws InterruptedException {
        Activity activity = mActivityRule.getActivity();
        activity.getSharedPreferences(Constants.SHARED_PREF, Activity.MODE_PRIVATE).edit().clear().apply();
        DBService.getDBService(activity);
        DBService.clearDB();
        Thread.sleep(1000);
    }

    @Test
    public void loginUser() throws InterruptedException {

        // Create Server
        String serverName = "Testserver 1";
        String serverAddress = "mge1.dev.ifs.hsr.ch";
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.serverName)).perform(typeText(serverName), closeSoftKeyboard());
        onView(withId(R.id.serverUri)).perform(typeText(serverAddress), closeSoftKeyboard());
        onView(withText(R.string.server_add_button)).perform(click());
        Thread.sleep(1000);
        onView(withText(mActivityRule.getActivity().getString(R.string.server_add_added, serverName))).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));

        // Registrate new user
        int id = (int)(Math.random() * 100000);
        onView(withText(serverName)).perform(click());
        onView(withText(R.string.login_register)).perform(click());
        onView(withId(R.id.matrikelField)).perform(typeText(String.valueOf(id)), closeSoftKeyboard());
        onView(withId(R.id.nameField)).perform(typeText("test" + String.valueOf(id)), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText("test" + String.valueOf(id) + "@hsr.ch"), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText("asdf"), closeSoftKeyboard());
        onView(withId(R.id.passwordRepField)).perform(typeText("asdf"), closeSoftKeyboard());
        onView(withText(R.string.registration_button)).perform(click());
        Thread.sleep(1000);
        onView(withText(R.string.registration_success)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));

        // Check Gadget-List
        onView(withId(R.id.gadgetlistRecyclerView)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                RecyclerView gadgetlist = (RecyclerView) view;
                int count = gadgetlist.getAdapter().getItemCount();
                org.junit.Assert.assertTrue("List has elements", count > 0);
            }
        });

        // Add reservation
        onView(withText("Android 3")).perform(click());
        onView(withText(R.string.reserve_gadget_button)).perform(click());
        onView(withText(R.string.reserve_gadget_success)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));
        Thread.sleep(1000);

        // Delete Reservation
        onView(withId(R.id.action_reservations)).perform(click());
        onView(withId(R.id.deleteButton)).perform(click());
        Thread.sleep(1000);
        onView(withText(R.string.reservation_deleted)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));

        // Change Server
        String server2Name = "Testserver 2";
        String server2Address = "mge2.dev.ifs.hsr.ch";
        onView(withId(R.id.action_server)).perform(click());
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.serverName)).perform(typeText(server2Name), closeSoftKeyboard());
        onView(withId(R.id.serverUri)).perform(typeText(server2Address), closeSoftKeyboard());
        onView(withText(R.string.server_add_button)).perform(click());
        Thread.sleep(1000);
        onView(withText(mActivityRule.getActivity().getString(R.string.server_add_added, server2Name))).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(server2Name)).perform(click());

        // Login
        onView(withId(R.id.emailEditText)).perform(typeText("m@hsr.ch"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("12345"), closeSoftKeyboard());
        onView(withText(R.string.login_button)).perform(click());
        Thread.sleep(1000);

        // Check Loans
        onView(withId(R.id.action_loans)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.loanListRecyclerView)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                RecyclerView gadgetlist = (RecyclerView) view;
                int count = gadgetlist.getAdapter().getItemCount();
                org.junit.Assert.assertTrue("List has elements", count > 0);
            }
        });
    }

    @After
    public void clearAllAfter() {
        Activity activity = mActivityRule.getActivity();
        activity.getSharedPreferences(Constants.SHARED_PREF, Activity.MODE_PRIVATE).edit().clear().apply();
        DBService.getDBService(activity);
        DBService.clearDB();
    }
}
