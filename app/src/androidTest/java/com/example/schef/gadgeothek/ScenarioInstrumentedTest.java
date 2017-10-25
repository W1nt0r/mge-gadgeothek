package com.example.schef.gadgeothek;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.example.schef.domain.Constants;
import com.example.schef.service.DBService;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScenarioInstrumentedTest extends InstrumentationTestCase {

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
    public void loginUser() {
        onView(withText("Testserver 1")).perform(click());
        onView(withId(R.id.emailEditText)).perform(typeText("m@hsr.ch"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("12345"), closeSoftKeyboard());
        onView(withText(R.string.login_button)).perform(click());
    }
}
