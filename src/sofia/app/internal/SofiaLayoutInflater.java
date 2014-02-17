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

package sofia.app.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;

//-------------------------------------------------------------------------
/**
 * The layout inflater used by Sofia {@link Screen} classes. It provides the
 * following enhancements:
 * <ul>
 * <li>Views specified in XML with simple names (e.g., {@code ListView} instead
 * of {@code android.widget.ListView}) will be inflated with a Sofia-enhanced
 * version from the {@code sofia.view} or {@code sofia.widget} packages if it
 * exists, falling back to the {@code android} packages if it does not.</li>
 * <li>Private fields whose names match the ID of a view and have compatible
 * types will be automatically assigned references to those widgets, as if
 * {@code findViewById} had been called.</li>
 * <li>View/widget events (such as click, item-selected, etc.) will be
 * automatically bound to methods in the {@link Screen} class if they have
 * matching names and types.</li>
 * </ul>
 *
 * @author Tony Allevato
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
