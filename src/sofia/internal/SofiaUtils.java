package sofia.internal;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Looper;
import android.os.MessageQueue;
import java.lang.reflect.Field;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.Context;

// -------------------------------------------------------------------------
/**
 * Various helper methods used in the Sofia library. Not intended to be used by
 * most users of the library.
 *
 * @author  Tony Allevato
 * @version 2011.10.15
 */
public class SofiaUtils
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public static void quitLoop()
    {
        // This is a major hack, which makes the main GUI message loop
        // quitable, sends a quit message to it that is then processed by
        // the "nested" loop to end it, then clears its "quit" flag so that
        // the "outer" loop can continue normally.
        //
        // Our hope is that the implementation of MessageQueue in Android
        // will remain mostly unchanged, but we will need to test this
        // thoroughly on all API versions that we plan to use, now and in
        // the future.

        try
        {
            Field mQuitAllowed =
                MessageQueue.class.getDeclaredField("mQuitAllowed");
            mQuitAllowed.setAccessible(true);

            mQuitAllowed.set(Looper.myQueue(), true);

            Looper.myLooper().quit();

            Field mQuiting =
                MessageQueue.class.getDeclaredField("mQuiting");
            mQuiting.setAccessible(true);

            mQuiting.set(Looper.myQueue(), false);
            mQuitAllowed.set(Looper.myQueue(), false);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Sofia modal runnables are not "
                + "supported on this version of the Android API because the "
                + "MessageQueue.mQuitAllowed field could not be found or "
                + "there was a problem changing it.");
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the value of a resource ID that is given by name.
     *
     * @param context the context
     * @param type the type of the resource
     * @param id the id to retrieve
     * @return the numeric identifier
     */
    public static int getResourceId(Context context, String type, String id)
    {
        Resources resources = context.getResources();
        return resources.getIdentifier(id, type, context.getPackageName());
    }
}
