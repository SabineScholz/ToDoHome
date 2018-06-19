package com.example.android.todohome.activities;


import android.database.Cursor;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.android.todohome.R;
import com.example.android.todohome.model.TaskContract;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private final int PERCENTAGE_DISPLAYED = 70;

    /*
    @Rule annotation:
    The annotated Activity will be launched before each annotated @Test and before any annotated @Before methods.
    The Activity is automatically terminated after the test is completed and all @After methods are finished.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);



    @Test
    public void addTask() throws Exception {

        String newTaskTitle = "Buy milk";
        String newTaskDescription = "Buy biological milk at the farm";

        // Click the add new task button
        onView(withId(R.id.add_new_button))    // Find the floating action button
                .check(matches(isDisplayed())) // check whether the button is displayed
                .perform(click());             // click the button

        // Check whether the editor is displayed
        onView(withId(R.id.edit_text_task_title))                              // Find the input text field for the task title
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));    // Check whether the input text field is displayed

        // Add task title and description
        // Type new task title
        onView(withId(R.id.edit_text_task_title)).perform(typeText(newTaskTitle), closeSoftKeyboard());
        // Type new task description
        onView(withId(R.id.edit_text_task_description)).perform(typeText(newTaskDescription), closeSoftKeyboard());

        // Save the task
        onView(withId(R.id.save_task_button)).perform(click());

        // Find the list view
        // Check whether the list contains the new task
        // Click on the task
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, newTaskTitle)).perform(click());

        // Check whether the task is displayed in the editor
        onView(withId(R.id.edit_text_task_title))
                .check(matches(withText(newTaskTitle)));
    }



    //@Test
    public void mainActivityTest() {


        onView(withId(R.id.add_new_button))    // Find the floating action button
                .check(matches(isDisplayed())) // check whether the button is displayed
                .perform(click());             // click the button



        onView(withId(R.id.edit_text_task_title))  // Find the input text field for the task title
                .check(matches(isDisplayed()))     // Check whether the input text field is displayed
                .perform(click())                  // Click inside the input text field
                .perform(typeText("Buy milk"),closeSoftKeyboard()); // Type text into the input text field


        onView(withId(R.id.save_task_button)).  // Find the floating action button that saves a task when clicked
                perform(click());               // Click the "save"-button

//        ViewInteraction listView = onView(
//                allOf(withId(R.id.list_view),
//                        childAtPosition(
//                                allOf(withId(R.id.list_fragment_container),
//                                        childAtPosition(
//                                                IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
//                                                0)),
//                                0),
//                        isDisplayed()));
//        listView.check(matches(isDisplayed()));
//
//        ViewInteraction textView = onView(
//                allOf(withId(R.id.name_text_view), withText("Buy milk"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.list_view),
//                                        0),
//                                1),
//                        isDisplayed()));
//        textView.check(matches(withText("Buy milk")));
//
//        DataInteraction linearLayout = onData(anything())
//                .inAdapterView(allOf(withId(R.id.list_view),
//                        childAtPosition(
//                                withId(R.id.list_fragment_container),
//                                1)))
//                .atPosition(0);
//        linearLayout.perform(click());

    }
}
