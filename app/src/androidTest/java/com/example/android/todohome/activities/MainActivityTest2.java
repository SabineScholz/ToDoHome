package com.example.android.todohome.activities;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.android.todohome.R;
import com.example.android.todohome.model.TaskContract;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest2 {

    private final int PERCENTAGE_DISPLAYED = 70;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        //onView(withText("Alle Aufgaben löschen")).perform(click());
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Beispieldaten einfügen")).perform(click());
    }

    @Test
    public void updateTaskTitle() {

        String title = "Groceries";
        String addition = " for the family";
        String titleUpdated = title + addition;

        // Check whether the task is present in the task list
        onView(withText(title)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));

        // Check whether the done checkbox is set to false
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).onChildView(withId(R.id.checkbox)).check(matches(not(isChecked())));

        // Click on task
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).perform(click());

        onView(withId(R.id.edit_text_task_title)).check(matches(withText(title)));



        // Update the task title
        onView(withId(R.id.edit_text_task_title)).perform(typeText(addition), closeSoftKeyboard());

        // Save the updated task
        onView(withId(R.id.save_task_button)).perform(click());

        // Check whether the updated title appears in the list
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, titleUpdated)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
