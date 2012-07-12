package sofia.app;

import sofia.app.internal.PersistenceManager;
import sofia.app.internal.ScreenMixin;
import sofia.app.internal.SofiaLayoutInflater;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

// -------------------------------------------------------------------------
/**
 * The {@code Screen} class represents a single screen in an Android
 * application.
 *
 * @author  Tony Allevato
 * @version 2011.10.08
 */
public abstract class Screen extends Activity
{
    //~ Instance/static variables ............................................

    private ScreenMixin mixin;
    private SofiaLayoutInflater layoutInflater;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new {@code Screen} object.
     */
    public Screen()
    {
        mixin = new ScreenMixin(this);
    }


    //~ Methods ..............................................................

    // ----------------------------------------------------------
    /**
     * Called before {@link #initialize()} during the screen creation process.
     * Most users typically will not need to override this method; it is
     * intended for Sofia's own subclasses of {@link Screen} so that users can
     * override {@link #initialize()} without being required to call the
     * superclass implementation.
     */
    protected void beforeInitialize()
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Called when the screen is created. Typically, initialization of the GUI
     * will occur in this method, by creating some views and then calling
     * {@link #setContentView(View)}.
     * </p><p>
     * In this method, the views have not yet been laid out, so you cannot
     * depend on the sizes or positions of those views to be correct (for
     * example, calling {@link View#getWidth()} would return 0). You should also
     * <b>not</b> call methods in {@code initialize()} that require the screen
     * to be fully set up (like {@link #selectItemFromList(String, List)} or
     * {@link #selectImageFromGallery()}). Call those in
     * {@link #afterInitialize()} instead.
     * </p>
     */
    //protected abstract void initialize();


    // ----------------------------------------------------------
    /**
     * Called once the screen has been created and made visible.
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
    public Object getSystemService(String service)
    {
    	if (LAYOUT_INFLATER_SERVICE.equals(service))
    	{
    		if (layoutInflater == null)
    		{
    			layoutInflater = new SofiaLayoutInflater(this);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	return mixin.onOptionsItemSelected(item)
    			|| super.onOptionsItemSelected(item);
    }


    // ----------------------------------------------------------
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
     * @param intent an {@code Intent} that describes the activity to start
     */
    public void presentActivity(Intent intent)
    {
        mixin.presentActivity(intent);
    }


    // ----------------------------------------------------------
    /**
     * Starts the activity represented by the specified {@code Screen} subclass
     * and slides it into view. This method will not return until the new
     * screen is dismissed by the user.
     *
     * @param screenClass the subclass of {@code Screen} that will be displayed
     * @param resultClass the class that represents the type of object that
     * @param args the arguments to be sent to the screen's {@code initialize}
     *     method
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
     * Called when a sub-activity returns yielding a result. Subclasses that
     * override this method <b>must</b> call the superclass implementation in
     * order to make sure that built-in methods like
     * {@link #selectImageFromGallery()} work correctly.
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
