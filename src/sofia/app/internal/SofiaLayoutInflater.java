package sofia.app.internal;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @version 2012.11.01
 */
public class SofiaLayoutInflater extends LayoutInflater
{
    //~ Fields ................................................................

    private static final String[] classPrefixList = {
        "sofia.widget.", "sofia.view.", "sofia.graphics.",
        "android.widget.", "android.webkit.", "android.view."
    };

    private Object receiver;
    private EventBinder eventBinder;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public SofiaLayoutInflater(Context context, Object receiver)
    {
        super(context);
        this.receiver = receiver;
        eventBinder = new EventBinder(receiver);
        setFactory(factory);
    }


    // ----------------------------------------------------------
    public SofiaLayoutInflater(LayoutInflater original, Context newContext,
            Object receiver)
    {
        super(original, newContext);
        this.receiver = receiver;
        eventBinder = new EventBinder(receiver);
        setFactory(factory);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public LayoutInflater cloneInContext(Context newContext)
    {
        return new SofiaLayoutInflater(this, newContext, receiver);
    }


    // ----------------------------------------------------------
    private void bindField(View view)
    {
        Class<?> receiverClass = receiver.getClass();

        String id = getIdName(getContext(), view.getId());

        if (id != null)
        {
            try
            {
                Field field = receiverClass.getDeclaredField(id);

                if (field.getType().isAssignableFrom(view.getClass()))
                {
                    field.setAccessible(true);
                    field.set(receiver, view);
                }
            }
            catch (Exception e)
            {
                // Do nothing.
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the string name of the specified resource ID.
     *
     * @param context the context that contains the resources
     * @param id the ID to look up
     * @return the string name of the ID, or null if it was not found
     */
    private static String getIdName(Context context, int id)
    {
        if (id != View.NO_ID)
        {
            return context.getResources().getResourceEntryName(id);
        }
        else
        {
            return null;
        }
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private Factory factory = new Factory()
    {
        @Override
        public View onCreateView(String name, Context context,
                AttributeSet attrs)
        {
            View view = null;

            if (name.indexOf('.') == -1)
            {
                for (String prefix : classPrefixList)
                {
                    try
                    {
                        view = createView(name, prefix, attrs);
                        break;
                    }
                    catch (ClassNotFoundException e)
                    {
                        // Do nothing.
                    }
                }
            }

            if (view == null)
            {
                try
                {
                    view = createView(name, null, attrs);
                }
                catch (ClassNotFoundException e)
                {
                    // Do nothing.
                }
            }

            if (view != null)
            {
                eventBinder.bindEvents(view, attrs);
                bindField(view);
            }

            return view;
        }
    };
}
