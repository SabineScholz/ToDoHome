package com.example.android.todohome.activities;


import android.database.Cursor;
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
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


/**
 * Test class to test CRUD operations on single tasks:
 *
 * 1. Adding tasks
 *   1.1 title and description (--> task saved)
 *   1.2 title only (--> task saved)
 *   1.3 description only (--> task not saved)
 *   1.4 no title, no description (--> task not saved)
 *
 * 2. Reading tasks
 *   2.1 tested in 1. already
 *
 * 3. Updating tasks
 *  3.1 change title in editor
 *  3.2 change description in editor
 *  3.3 change done/not done in editor
 *  3.4 change done/not done in task list
 *
 * 4. Deleting tasks
 *  4.1 delete task in editor
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private final int PERCENTAGE_DISPLAYED = 70;
    private static String newTaskTitle = "Buy milk";
    private static String newTaskDescription = "Buy biological milk at the farm";

    /*
    @Rule annotation:
    The annotated Activity will be launched before each annotated @Test and before any annotated @Before methods.
    The Activity is automatically terminated after the test is completed and all @After methods are finished.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Alle Aufgaben löschen")).perform(click());
//
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
//        onView(withText("Beispieldaten einfügen")).perform(click());


        onView(withText("Löschen")).perform(click());


        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.title), withText("Beispieldaten einfügen"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView3.perform(click());
    }

    /**
     * Test 1.1
     * Adding a task, providing title and description
     * Start point: list of tasks
     * @throws Exception
     */
    @Test
    public void addTaskWithTitleAndDescription() throws Exception {

        // Click the add new task button
        onView(withId(R.id.add_new_button))    // Find the floating action button
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED))) // check whether the button is displayed
                .perform(click());             // click the button

        // Check whether the editor (incl. the title text field) is displayed
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

    /**
     * Test 1.2
     * Adding a task, providing title only
     * Start point: list of tasks
     * @throws Exception
     */
    @Test
    public void addTaskWithTitleOnly() throws Exception {

        // Click the add new task button
        onView(withId(R.id.add_new_button))    // Find the floating action button
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED))) // check whether the button is displayed
                .perform(click());             // click the button

        // Check whether the editor (incl. the title text field) is displayed
        onView(withId(R.id.edit_text_task_title))                              // Find the input text field for the task title
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));    // Check whether the input text field is displayed

        // Add task title and description
        // Type new task title
        onView(withId(R.id.edit_text_task_title)).perform(typeText(newTaskTitle), closeSoftKeyboard());

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

    /**
     * Test 1.3
     * Adding a task, providing description only
     * Start point: list of tasks
     * @throws Exception
     */
    @Test
    public void addTaskWithDescriptionOnly() throws Exception {

        // Click the add new task button
        onView(withId(R.id.add_new_button))    // Find the floating action button
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED))) // check whether the button is displayed
                .perform(click());             // click the button

        // Check whether the editor (incl. the title text field) is displayed
        onView(withId(R.id.edit_text_task_title))                              // Find the input text field for the task title
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));    // Check whether the input text field is displayed

        // Type new task description
        onView(withId(R.id.edit_text_task_description)).perform(typeText(newTaskDescription), closeSoftKeyboard());

        // Save the task
        onView(withId(R.id.save_task_button)).perform(click());

        // Check whether we are still in the editor and not in the list view
        onView(withId(R.id.edit_text_task_title))
                .check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));
    }

    /**
     * Test 3.1
     * change title in editor
     */
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

        // Check whether the correct title is shown
        onView(withId(R.id.edit_text_task_title)).check(matches(withText(title)));

        // Update the task title
        onView(withId(R.id.edit_text_task_title)).perform(typeText(addition), closeSoftKeyboard());

        // Save the updated task
        onView(withId(R.id.save_task_button)).perform(click());

        // Check whether the updated title appears in the list
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, titleUpdated)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));
    }


    /**
     * Test 3.2
     * change description in editor
     */
    @Test
    public void updateTaskDescription() {

        String title = "Groceries";
        String description = "Doing groceries for the weekend";
        String newDescription = "Visit the supermarket";

        // Check whether the task is present in the task list
        onView(withText(title)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));

        // Click on task
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).perform(click());

        // Check whether the correct description is shown
        onView(withId(R.id.edit_text_task_description)).check(matches(withText(description)));

        // Update the task description
        onView(withId(R.id.edit_text_task_description)).
                perform(clearText()).
                perform(typeText(newDescription), closeSoftKeyboard());

        // Save the updated task
        onView(withId(R.id.save_task_button)).perform(click());

        // Check whether the updated description appears in the list
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION, newDescription)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));
    }

    /**
     * Test 3.3
     * change done/not done in editor
     */
    @Test
    public void updateTaskDone() {

        String title = "Groceries";

        // Check whether the task is present in the task list
        onView(withText(title)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));

        // Check whether the done checkbox is set to false
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).onChildView(withId(R.id.checkbox)).check(matches(not(isChecked())));

        // Click on task
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).perform(click());

        // Set the task to done
        onView(withId(R.id.done_checkbox)).perform(click());

        // Save the updated task
        onView(withId(R.id.save_task_button)).perform(click());

        // Check whether the task appears as done in the list
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).onChildView(withId(R.id.checkbox)).check(matches(isChecked()));
    }

    /**
     * Test 3.4
     * change done/not done in the list view
     */
    @Test
    public void updateTaskDoneInList() {

        String title = "Groceries";

        // Check whether the task is present in the task list
        onView(withText(title)).check(matches(isDisplayingAtLeast(PERCENTAGE_DISPLAYED)));

        // Check whether the done checkbox is set to false
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).onChildView(withId(R.id.checkbox)).check(matches(not(isChecked())));

        // Mark the task as done
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).onChildView(withId(R.id.checkbox)).perform(click());

        // Check whether the task appears as done in the list
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).onChildView(withId(R.id.checkbox)).check(matches(isChecked()));

        // Click on task
        onData(CursorMatchers.withRowString(TaskContract.TaskEntry.COLUMN_TASK_NAME, title)).perform(click());

        // Check whether the task is done
        onView(withId(R.id.done_checkbox)).check(matches(isChecked()));
    }

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
