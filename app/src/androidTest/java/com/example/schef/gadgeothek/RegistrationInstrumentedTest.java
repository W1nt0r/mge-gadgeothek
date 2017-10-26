package com.example.schef.gadgeothek;


import android.app.Activity;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.schef.domain.Constants;
import com.example.schef.service.DBService;

import org.hamcrest.CoreMatchers;
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
public class RegistrationInstrumentedTest {

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
    }

    @Test
    public void testRegistrationNoSuccess() throws InterruptedException {
        onView(withText("Testserver 1")).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.matrikelField)).perform(typeText("444"), closeSoftKeyboard());
        onView(withId(R.id.emailField)).perform(typeText("miep@miep.miep"), closeSoftKeyboard());
        onView(withId(R.id.nameField)).perform(typeText("miep2"), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(typeText("miep2"), closeSoftKeyboard());
        onView(withId(R.id.passwordRepField)).perform(typeText("miep2"), closeSoftKeyboard());
        onView(withId(R.id.registrationButton)).perform(click());

        Thread.sleep(1000);

        onView(withText(R.string.registration_no_success)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(mActivityRule.getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(isDisplayed()));
    }
}
