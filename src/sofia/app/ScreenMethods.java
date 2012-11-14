package sofia.app;

import android.app.Activity;
import android.content.Intent;

//-------------------------------------------------------------------------
/**
 * <p>
 * Lists the common methods that are provided by classes like {@link Screen},
 * {@link MapScreen}, and other "{@code Screen}-like" classes that cannot be
 * in the same class hierarchy as {@code Screen}.
 * </p>
 *
 * @author  Tony Allevato
 * @version 2012.11.13
 */
public interface ScreenMethods
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Displays an alert dialog and waits for the user to dismiss it.
     *
     * @param title the title to display in the dialog
     * @param message the message to display in the dialog
     */
    void showAlertDialog(String title, String message);


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
    boolean showConfirmationDialog(String title, String message);


    // ----------------------------------------------------------
    /**
     * Starts the activity with the specified intent. This method will not
     * return until the new activity is dismissed by the user.
     *
     * @param intent an {@link Intent} that describes the activity to start
     * @param returnMethod the name of the method to call when the activity
     *     returns
     */
    void presentActivity(Intent intent, String returnMethod);


    // ----------------------------------------------------------
    /**
     * Starts the activity represented by the specified screen class
     * and slides it into view. This method returns immediately even as the
     * screen is being presented, but at that point a new screen has taken
     * over the user's attention and the old one might be discarded from memory
     * at any time. Therefore, users should typically not do any important
     * computation after calling this method.
     *
     * @param screenClass the screen that will be displayed
     * @param args the arguments to be sent to the new screen's
     *     {@code initialize} method
     */
    void presentScreen(Class<? extends Activity> screenClass, Object... args);


    // ----------------------------------------------------------
    /**
     * Call this method when the current screen is finished and should be
     * closed. The specified value will be passed back to the previous screen
     * and returned from the {@link #presentScreen(Class, Object...)} call that
     * originally presented this screen.
     *
     * @param result the value to pass back to the previous screen
     */
    void finish(Object result);
}
