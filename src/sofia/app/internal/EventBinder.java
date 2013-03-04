package sofia.app.internal;

import sofia.internal.events.EventDispatcher;
import sofia.internal.events.OptionalEventDispatcher;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @version 2012.11.02
 */
public class EventBinder
{
    //~ Fields ................................................................

    private static final String ANDROID_NS =
            "http://schemas.android.com/apk/res/android";


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Prevent instantiation.
     */
    private EventBinder()
    {
        // Do nothing.
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Binds any Sofia event handlers that apply to the specified view.
     *
     * @param view the view to bind events to
     * @param attrs the attribute set that was used during inflation (may be
     *     null if this is called outside of the inflater)
     */
    public static void bindEvents(View view, AttributeSet attrs)
    {
        bindOnClick(view, attrs);
        bindOnItemClick(view, attrs);
        bindOnItemSelected(view, attrs);
        bindOnEditingDone(view, attrs);
    }


    // ----------------------------------------------------------
    /**
     * This method implements the following binding rule: if a view is
     * clickable, and it does not already have a method bound to the onClick
     * attribute, then connect it to the context method "${id}Clicked", where
     * "${id}" is the string name of the view's identifier.
     *
     * @param view the view
     * @param attrs the attribute set
     */
    private static void bindOnClick(final View view, AttributeSet attrs)
    {
        if (!AdapterView.class.isAssignableFrom(view.getClass())
                && view.isClickable()
                && (attrs == null ||
                    attrs.getAttributeValue(ANDROID_NS, "onClick") == null))
        {
            final String id = getIdName(view.getContext(), view.getId());

            if (id != null)
            {
                final OptionalEventDispatcher event =
                        new OptionalEventDispatcher(id + "Clicked", 0);

                try
                {
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {
                            event.dispatch(v.getContext(), v);
                        }
                    });
                }
                catch (Exception e)
                {
                    // Do nothing. This try/catch was placed here because it
                    // was discovered that setOnClickListener on an AdapterView
                    // throws an exception with a message directing you to use
                    // setOnItemClickListener instead. For future-proofing, we
                    // want to catch any such possible exceptions and log them
                    // so that we can place appropriate checks at the front of
                    // this method.

                    Log.d("EventBinder", e.getMessage());
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * This method implements the following binding rule: for each AbsListView,
     * attach an OnItemClickListener to it that calls the context method
     * "${id}ItemClicked", where "${id}" is the string name of the view's
     * identifier.
     *
     * @param view the view
     * @param attrs the attribute set
     */
    private static void bindOnItemClick(final View view, AttributeSet attrs)
    {
        if (view instanceof AbsListView)
        {
            AbsListView adapterView = (AbsListView) view;

            // Fall back to "listView" as a default handler prefix if the list
            // doesn't have its own ID -- this supports the automatically
            // created ListView on a ListScreen.
            String resourceId = getIdName(view.getContext(), view.getId());
            final String id = (resourceId != null) ? resourceId : "listView";

            final OptionalEventDispatcher event =
                    new OptionalEventDispatcher(id + "ItemClicked", 1);

            adapterView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent,
                        View view, int position, long id)
                {
                    Object item = parent.getAdapter().getItem(position);
                    event.dispatch(parent.getContext(), item, position);
                }
            });
        }
    }


    // ----------------------------------------------------------
    /**
     * This method implements the following binding rule: for each AbsSpinner,
     * attach an OnItemSelectedListener to it that calls the context method
     * "${id}ItemSelected", where "${id}" is the string name of the view's
     * identifier.
     *
     * @param view the view
     * @param attrs the attribute set
     */
    private static void bindOnItemSelected(final View view, AttributeSet attrs)
    {
        if (view instanceof AbsSpinner)
        {
            AbsSpinner adapterView = (AbsSpinner) view;

            final String id = getIdName(view.getContext(), view.getId());

            if (id != null)
            {
                final OptionalEventDispatcher itemEvent =
                        new OptionalEventDispatcher(id + "ItemSelected", 1);
                final EventDispatcher nothingEvent =
                        new EventDispatcher(id + "NothingSelected");

                adapterView.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id)
                    {
                        Object item = parent.getAdapter().getItem(position);
                        itemEvent.dispatch(
                                parent.getContext(), item, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                        nothingEvent.dispatch(parent.getContext());
                    }
                });
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * This method implements the following binding rule: for each EditText
     * view, attach an OnEditorActionListener to it that calls the context
     * method "${id}EditingDone", where "${id}" is the string name of the
     * view's identifier.
     *
     * @param view the view
     * @param attrs the attribute set
     */
    private static void bindOnEditingDone(final View view, AttributeSet attrs)
    {
        if (view instanceof EditText)
        {
            EditText editText = (EditText) view;
            editText.setOnEditorActionListener(editorActionListener);
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


    //~ Private listeners .....................................................

    // ----------------------------------------------------------
    private static final TextView.OnEditorActionListener editorActionListener =
            new TextView.OnEditorActionListener()
    {
        // ----------------------------------------------------------
        @Override
        public boolean onEditorAction(TextView v, int actionId,
                KeyEvent event)
        {
            // TODO We need to test this across multiple devices with both soft
            // and hard keyboards.

            if (actionMatchesImeOptions(v, actionId)
                    || (event != null
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
            {
                dispatchEvent(v);

                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager imm = (InputMethodManager)
                            v.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                return true;
            }
            else
            {
                return false;
            }
        }


        // ----------------------------------------------------------
        /**
         * By default, if the user does not explicitly set any IME options on
         * a text view, it sends either the Next or Done event depending on
         * its position in the GUI layout. We want to capture both of those in
         * that case. Otherwise, if the user has explicitly set a specific
         * action through {@link TextView#setImeOptions(int)}, then we only
         * want to capture that specific one to dispatch an event.
         *
         * @param v the text view
         * @param actionId the IME action
         *
         * @return true if the IME action matches the options of the text view
         */
        private boolean actionMatchesImeOptions(TextView v, int actionId)
        {
            int options = v.getImeOptions();

            if (options == 0)
            {
                return actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE;
            }
            else
            {
                return actionId == (options & EditorInfo.IME_MASK_ACTION);
            }
        }


        // ----------------------------------------------------------
        /**
         * Dispatch the "EditingDone" event to the text view's context.
         *
         * @param v the text view
         */
        private void dispatchEvent(TextView v)
        {
            final String id = getIdName(v.getContext(), v.getId());

            if (id != null)
            {
                final OptionalEventDispatcher event =
                        new OptionalEventDispatcher(id + "EditingDone");
                event.dispatch(v.getContext(), v);
            }
        }
    };
}
