package hslu.bda.medimemory.fragment;

/**
 * Created by Loana on 07.03.2016.
 */
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hslu.bda.medimemory.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FragmentRegistrationTest extends ActivityInstrumentationTestCase2 {
    private FragmentRegistration fragmentRegistration;

    public FragmentRegistrationTest(){
        super(MainActivity.class);
    }
    @Before
    public void setup() throws Exception {
        super.setUp();
    }



    @Test
    public void validateEditText(){
        onView(withId(R.id.edit_name)).perform(typeText("Pillname")).check(matches(withText("Pillname")));
    }
}
