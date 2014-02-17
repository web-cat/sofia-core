/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.app;

import sofia.app.internal.PersistenceManager;
import sofia.app.internal.ScreenMixin;
import sofia.app.internal.SofiaLayoutInflater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

// -------------------------------------------------------------------------
/**
 * The {@code Screen} class represents a single screen in an Android
 * application. A {@code Screen} is a subclass of Android's notion of an
 * {@link Activity}, which manages a user interface and acts as a "controller"
 * for the events that occur in that GUI.
 *
 * @author Tony Allevato
 */
public abstract class Screen
    extends Activity
    implements ScreenMethods
{
    //~ Fields ................................................................

    private ScreenMixin mixin;
    private SofiaLayoutInflater layoutInflater;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Do not directly create instances of the {@code Screen} class; They are
     * created for you by the operating system.
     */
    public Screen()
    {
        mixin = new ScreenMixin(this);
    }


    //~ Methods ..............................................................

    // ----------------------------------------------------------
    /**
     * Called before {@code initialize()} during the screen creation process.
     * Most users typically will not need to override this method; it is
     * intended for Sofia's own subclasses of {@link Screen} so that they can
     * provide additional functionality before {@code initialize()}, and then
     * users can override {@code initialize()} without being required to call
     * the superclass implementation.
     */
    protected void beforeInitialize()
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    /**
     * Called when the screen has been created and is about to be displayed on
     * the device.
     */
    public void initialize()
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    /**
     * Called once the screen has been created and made visible. Most users
     * will not need to override this method; it is provided so that Sofia's
     * own {@code Screen} subclasses can do additional initialization after the
     * user's own {@code initialize()} method has executed, if necessary.
     */
    protected void afterInitialize()
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Determines whether the user's {@link #initialize()} method should be
     * postponed until layout of the views has already occurred.
     * </p><p>
     * In most cases, the default behavior of false is preferred, which will
     * cause {@link #initialize()} to be called during the activity's
     * {@link #onCreate(Bundle)} method, where the view hierarchy of the
     * activity can be created. But some specialized subclasses of
     * {@code Screen}, like {@link ShapeScreen}, postpone the call to
     * {@code initialize()} until later, after its {@code ShapeView} has
     * already been laid out, so that users can access the width and height of
     * the view in order lay out shapes using that information in calculations.
     * </p>
     *
     * @return true if {@link #initialize()} should be postponed until the
     *     screen's views are already laid out, or false to call it immediately
     *     in {@link #onCreate(Bundle)}
     */
    protected boolean doInitializeAfterLayout()
    {
        return false;
    }


    // ----------------------------------------------------------
    @Override
    public LayoutInflater getLayoutInflater()
    {
        return (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }


    // ----------------------------------------------------------
    /**
     * This method is overridden to replace the default Android layout inflater
     * with one that supports Sofia's enhancements.
     */
    @Override
    public Object getSystemService(String service)
    {
        if (LAYOUT_INFLATER_SERVICE.equals(service))
        {
            if (layoutInflater == null)
            {
                LayoutInflater inflater =
                        (LayoutInflater) super.getSystemService(service);
                layoutInflater = new SofiaLayoutInflater(inflater, this, this);
            }

            return layoutInflater;
        }
        else
        {
            return super.getSystemService(service);
        }
    }


    // ----------------------------------------------------------
    /**
     * This method is called after an attempted was made to inflate the
     * screen's layout. Most users will not need to call or override this
     * method; it is provided for Sofia's own subclasses of {@code Screen} to
     * support custom behavior depending on whether a user layout was provided
     * or not.
     *
     * @return true if a layout was found and inflated, otherwise false
     */
    protected void afterLayoutInflated(boolean inflated)
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState instance data previously saved by this
     *     activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mixin.restoreInstanceState(savedInstanceState);

        // Grab the input arguments, if there were any.
        final Object[] args = mixin.getScreenArguments(getIntent());

        beforeInitialize();
        afterLayoutInflated(mixin.tryToInflateLayout());

        if (!doInitializeAfterLayout())
        {
            mixin.invokeInitialize(args);
        }

        // Post a call to afterInitialize() in the message queue so that it
        // gets called as soon as possible after the screen has been made
        // visible.

        getWindow().getDecorView().post(new Runnable()
        {
            public void run()
            {
                if (doInitializeAfterLayout())
                {
                    mixin.invokeInitialize(args);
                }

                afterInitialize();
            }
        });
    }


    // ----------------------------------------------------------
    @Override
    protected void onStop()
    {
        PersistenceManager.getInstance().savePersistentContext(this);
        super.onStop();
    }


    // ----------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();
        mixin.runResumeInjections();
    }


    // ----------------------------------------------------------
    @Override
    protected void onPause()
    {
        mixin.runPauseInjections();
        super.onPause();
    }


    // ----------------------------------------------------------
    @Override
    protected void onDestroy()
    {
        mixin.runDestroyInjections();
        super.onDestroy();
    }


    // ----------------------------------------------------------
    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        mixin.saveInstanceState(bundle);
        super.onSaveInstanceState(bundle);
    }


    // ----------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return mixin.onCreateOptionsMenu(menu)
                || super.onCreateOptionsMenu(menu);
    }


    // ----------------------------------------------------------
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        return mixin.onMenuItemSelected(featureId, item)
                || super.onMenuItemSelected(featureId, item);
    }


    // ----------------------------------------------------------
    /**
     * Not intended to be called by users; this method is public as an
     * implementation detail.
     */
    public ScreenMixin getScreenMixin()
    {
        return mixin;
    }


    // ----------------------------------------------------------
    /**
     * Prints an informational message to the system log, tagged with the
     * "User Log" tag so that it can be easily identified in the LogCat view.
     *
     * @param message the message to log
     */
    public void log(String message)
    {
        Log.i("User Log", message);
    }


    // ----------------------------------------------------------
    /**
     * Displays an alert dialog and waits for the user to dismiss it.
     *
     * @param title the title to display in the dialog
     * @param message the message to display in the dialog
     */
    public void showAlertDialog(String title, String message)
    {
        mixin.showAlertDialog(title, message);
    }


    // ----------------------------------------------------------
    /**
     * Displays a confirmation dialog and waits for the user to select an
     * option.
     *
     * @param title the title to display in the dialog
     * @param message the message to display in the dialog
     * @return true if the user clicked the "Yes" option; false if the user
     *     clicked the "No" option or cancelled the dialog (for example, by
     *     pressing the Back button)
     */
    public boolean showConfirmationDialog(String title, String message)
    {
        return mixin.showConfirmationDialog(title, message);
    }


    // ----------------------------------------------------------
    /**
     * Display a popup list to the user and waits for them to select an item
     * from it. Items in the list will be rendered simply by calling the
     * {@link Object#toString()} method. To control the item renderer used to
     * display the list, see
     * {@link #selectItemFromList(String, List, ItemRenderer)}.
     *
     * @param <Item> the type of items in the list, which is inferred from the
     *     {@code list} parameter
     * @param title the title of the popup dialog
     * @param list the list of items to display in the popup
     * @return the item that was selected from the list, or null if the dialog
     *     was cancelled
     */
    /*public <Item> Item selectItemFromList(
        String title, List<? extends Item> list)
    {
        return selectItemFromList(title, list, new SimpleItemRenderer());
    }*/


    // ----------------------------------------------------------
    /**
     * Display a popup list to the user and waits for them to select an item
     * from it.
     *
     * @param <Item> the type of items in the list, which is inferred from the
     *     {@code list} parameter
     * @param title the title of the popup dialog
     * @param list the list of items to display in the popup
     * @param itemRenderer the item renderer to use to display each item
     * @return the item that was selected from the list, or null if the dialog
     *     was cancelled
     */
    /*public <Item> Item selectItemFromList(
        String title,
        List<? extends Item> list,
        ItemRenderer itemRenderer)
    {
        return internals.selectItemFromList(title, list, itemRenderer);
    }*/


    // ----------------------------------------------------------
    /**
     * Starts the activity with the specified intent. This method will not
     * return until the new activity is dismissed by the user.
     *
     * @param intent an {@link Intent} that describes the activity to start
     * @param returnMethod the name of the method to call when the activity
     *     returns
     */
    public void presentActivity(Intent intent, String returnMethod)
    {
        mixin.presentActivity(intent, returnMethod);
    }


    // ----------------------------------------------------------
    /**
     * Starts the activity represented by the specified {@code Screen} subclass
     * and slides it into view. This method returns immediately even as the
     * screen is being presented, but at that point a new screen has taken
     * over the user's attention and the old one might be discarded from memory
     * at any time. Therefore, users should typically not do any important
     * computation after calling this method.
     *
     * @param screenClass the subclass of {@code Screen} that will be displayed
     * @param args the arguments to be sent to the new screen's
     *     {@code initialize} method
     */
    public void presentScreen(
            Class<? extends Activity> screenClass, Object... args)
    {
        mixin.presentScreen(screenClass, args);
    }


    // ----------------------------------------------------------
    /**
     * Call this method when the current screen is finished and should be
     * closed. The specified value will be passed back to the previous screen
     * and returned from the {@link #presentScreen(Class, Object...)} call that
     * originally presented this screen.
     *
     * @param result the value to pass back to the previous screen
     */
    public void finish(Object result)
    {
        mixin.finish(result);
    }


    // ----------------------------------------------------------
    /**
     * Called when a sub-activity returns yielding a result. Subclasses can
     * override this method if they want to handle sub-activities in the
     * traditional way, but they <b>must</b> call the superclass implementation
     * in order to make sure that Sofia's built-in methods and choosers work
     * correctly.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data)
    {
        mixin.handleOnActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
